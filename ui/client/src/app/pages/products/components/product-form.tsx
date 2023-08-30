import React, { useContext } from "react";
import { AxiosError, AxiosResponse } from "axios";
import { object, string } from "yup";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";

import {
  ActionGroup,
  Button,
  ButtonVariant,
  Form,
} from "@patternfly/react-core";

import { Product } from "@app/api/models";
import { duplicateFieldCheck } from "@app/utils/utils";
import {
  useCreateProductMutation,
  useFetchProducts,
  useUpdateProductMutation,
} from "@app/queries/products";

import {
  HookFormPFTextArea,
  HookFormPFTextInput,
} from "@app/shared/components/HookFormPFFields";
import { NotificationsContext } from "@app/shared/components/NotificationsContext";

export interface FormValues {
  name: string;
  description?: string;
}

export interface IProductFormProps {
  product?: Product;
  onClose: () => void;
}

export const ProductForm: React.FC<IProductFormProps> = ({
  product,
  onClose,
}) => {
  const { pushNotification } = useContext(NotificationsContext);

  const { result: products } = useFetchProducts();

  const validationSchema = object().shape({
    name: string()
      .trim()
      .required()
      .min(3)
      .max(120)
      .matches(/[a-z0-9]([-a-z0-9]*[a-z0-9])?/)
      .test(
        "Duplicate name",
        "A product with this name address already exists. Use a different name.",
        (value) =>
          duplicateFieldCheck("name", products, product || null, value || "")
      ),
    description: string().trim().max(250),
  });

  const {
    handleSubmit,
    formState: { isSubmitting, isValidating, isValid, isDirty },
    getValues,
    control,
  } = useForm<FormValues>({
    defaultValues: {
      name: product?.name || "",
      description: product?.description || "",
    },
    resolver: yupResolver(validationSchema),
    mode: "onChange",
  });

  const onCreateProductSuccess = (_: AxiosResponse<Product>) =>
    pushNotification({
      title: "Product created",
      variant: "success",
    });

  const onCreateProductError = (error: AxiosError) => {
    pushNotification({
      title: "Error while creating product",
      variant: "danger",
    });
  };

  const { mutate: createProduct } = useCreateProductMutation(
    onCreateProductSuccess,
    onCreateProductError
  );

  const onUpdateProductSuccess = (_: AxiosResponse<Product>) =>
    pushNotification({
      title: "Product saved",
      variant: "success",
    });

  const onUpdateProductError = (error: AxiosError) => {
    pushNotification({
      title: "Error while saving data",
      variant: "danger",
    });
  };
  const { mutate: updateProduct } = useUpdateProductMutation(
    onUpdateProductSuccess,
    onUpdateProductError
  );

  const onSubmit = (formValues: FormValues) => {
    const payload: Product = {
      name: formValues.name.trim(),
      description: formValues.description?.trim(),
    };

    if (product) {
      updateProduct({ ...payload });
    } else {
      createProduct(payload);
    }
    onClose();
  };

  return (
    <Form onSubmit={handleSubmit(onSubmit)}>
      <HookFormPFTextInput
        control={control}
        name="name"
        label="Name"
        fieldId="name"
        isRequired
      />
      <HookFormPFTextArea
        control={control}
        name="description"
        label="Description"
        fieldId="description"
        resizeOrientation="vertical"
      />

      <ActionGroup>
        <Button
          type="submit"
          aria-label="submit"
          id="product-form-submit"
          variant={ButtonVariant.primary}
          isDisabled={!isValid || isSubmitting || isValidating || !isDirty}
        >
          {!product ? "Create" : "Save"}
        </Button>
        <Button
          type="button"
          id="cancel"
          aria-label="cancel"
          variant={ButtonVariant.link}
          isDisabled={isSubmitting || isValidating}
          onClick={onClose}
        >
          Cancel
        </Button>
      </ActionGroup>
    </Form>
  );
};
