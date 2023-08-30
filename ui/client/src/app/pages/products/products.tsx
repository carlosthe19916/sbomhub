import React, { useContext, useState } from "react";
import { NavLink } from "react-router-dom";
import { AxiosError } from "axios";

import {
  Button,
  ButtonVariant,
  Modal,
  ModalVariant,
  PageSection,
  PageSectionVariants,
  Text,
  TextContent,
  Toolbar,
  ToolbarContent,
  ToolbarGroup,
  ToolbarItem,
} from "@patternfly/react-core";
import {
  ActionsColumn,
  Table,
  Tbody,
  Td,
  Th,
  Thead,
  Tr,
} from "@patternfly/react-table";

import { SimplePagination } from "@app/shared/components/SimplePagination";
import {
  FilterToolbar,
  FilterType,
} from "@app/shared/components/FilterToolbar";
import {
  ConditionalTableBody,
  TableHeaderContentWithControls,
  TableRowContentWithControls,
} from "@app/shared/components/TableControls";

import { useLocalTableControls } from "@app/shared/hooks/table-controls";
import {
  useDeleteProductMutation,
  useFetchProducts,
} from "@app/queries/products";

import { Product } from "@app/api/models";
import { ProductForm } from "./components/product-form";
import { ConfirmDialog } from "@app/shared/components/ConfirmDialog";
import { NotificationsContext } from "@app/shared/components/NotificationsContext";
import { getAxiosErrorMessage } from "@app/utils/utils";

export const Products: React.FC = () => {
  const { pushNotification } = useContext(NotificationsContext);

  const [isConfirmDialogOpen, setIsConfirmDialogOpen] =
    useState<Boolean>(false);
  const [productIdToDelete, setProductIdToDelete] = React.useState<string>();

  const [createUpdateModalState, setCreateUpdateModalState] = useState<
    "create" | Product | null
  >(null);
  const isCreateUpdateModalOpen = createUpdateModalState !== null;
  const productToUpdate =
    createUpdateModalState !== "create" ? createUpdateModalState : null;

  //
  const onDeleteStakeholderSuccess = (response: any) => {
    pushNotification({
      title: "Product deleted",
      variant: "success",
    });
  };

  const onDeleteStakeholderError = (error: AxiosError) => {
    pushNotification({
      title: getAxiosErrorMessage(error),
      variant: "danger",
    });
  };

  //

  const {
    result: products,
    isFetching,
    fetchError,
    refetch,
  } = useFetchProducts();

  const { mutate: deleteProduct } = useDeleteProductMutation(
    onDeleteStakeholderSuccess,
    onDeleteStakeholderError
  );

  const tableControls = useLocalTableControls({
    idProperty: "name",
    items: products,
    columnNames: {
      name: "Name",
      description: "Description",
    },
    hasActionsColumn: true,
    filterCategories: [
      {
        key: "q",
        title: "Name",
        type: FilterType.search,
        placeholderText: "Search",
      },
    ],
    sortableColumns: ["name"],
    getSortValues: (item) => ({
      name: item?.name || "",
    }),
    hasPagination: true,
  });

  const {
    currentPageItems,
    numRenderedColumns,
    propHelpers: {
      toolbarProps,
      filterToolbarProps,
      paginationToolbarItemProps,
      paginationProps,
      tableProps,
      getThProps,
      getTdProps,
    },
  } = tableControls;

  const closeCreateUpdateModal = () => {
    setCreateUpdateModalState(null);
    refetch;
  };

  const deleteRow = (row: Product) => {
    setProductIdToDelete(row.name);
    setIsConfirmDialogOpen(true);
  };

  return (
    <>
      <PageSection variant={PageSectionVariants.light}>
        <TextContent>
          <Text component="h1">Products</Text>
          <Text component="p">List of products of your organization.</Text>
        </TextContent>
      </PageSection>
      <PageSection>
        <div
          style={{
            backgroundColor: "var(--pf-v5-global--BackgroundColor--100)",
          }}
        >
          <Toolbar {...toolbarProps}>
            <ToolbarContent>
              <FilterToolbar {...filterToolbarProps} />
              <ToolbarGroup variant="button-group">
                <ToolbarItem>
                  <Button
                    type="button"
                    id="create-product"
                    aria-label="Create new product"
                    variant={ButtonVariant.primary}
                    onClick={() => setCreateUpdateModalState("create")}
                  >
                    Create new
                  </Button>
                </ToolbarItem>
              </ToolbarGroup>
              <ToolbarItem {...paginationToolbarItemProps}>
                <SimplePagination
                  idPrefix="products-table"
                  isTop
                  paginationProps={paginationProps}
                />
              </ToolbarItem>
            </ToolbarContent>
          </Toolbar>

          <Table {...tableProps} aria-label="Products table">
            <Thead>
              <Tr>
                <TableHeaderContentWithControls {...tableControls}>
                  <Th {...getThProps({ columnKey: "name" })} />
                  <Th {...getThProps({ columnKey: "description" })} />
                </TableHeaderContentWithControls>
              </Tr>
            </Thead>
            <ConditionalTableBody
              isLoading={isFetching}
              isError={!!fetchError}
              isNoData={products.length === 0}
              numRenderedColumns={numRenderedColumns}
            >
              {currentPageItems?.map((item, rowIndex) => {
                return (
                  <Tbody key={item.name}>
                    <Tr>
                      <TableRowContentWithControls
                        {...tableControls}
                        item={item}
                        rowIndex={rowIndex}
                      >
                        <Td width={15} {...getTdProps({ columnKey: "name" })}>
                          <NavLink to={`/products/${item.name}`}>
                            {item.name}
                          </NavLink>
                        </Td>
                        <Td
                          width={20}
                          modifier="truncate"
                          {...getTdProps({ columnKey: "description" })}
                        >
                          {item.description}
                        </Td>
                        <Td isActionCell>
                          <ActionsColumn
                            items={[
                              {
                                title: "Edit",
                                onClick: () => setCreateUpdateModalState(item),
                              },
                              {
                                title: "Delete",
                                onClick: () => deleteRow(item),
                              },
                            ]}
                          />
                        </Td>
                      </TableRowContentWithControls>
                    </Tr>
                  </Tbody>
                );
              })}
            </ConditionalTableBody>
          </Table>

          <SimplePagination
            idPrefix="dependencies-table"
            isTop={false}
            paginationProps={paginationProps}
          />
        </div>
      </PageSection>

      <Modal
        id="create-edit-product-modal"
        title={productToUpdate ? "Update product" : "New product"}
        variant={ModalVariant.medium}
        isOpen={isCreateUpdateModalOpen}
        onClose={closeCreateUpdateModal}
      >
        <ProductForm
          product={productToUpdate ? productToUpdate : undefined}
          onClose={closeCreateUpdateModal}
        />
      </Modal>

      {isConfirmDialogOpen && (
        <ConfirmDialog
          title={"Delete product"}
          isOpen={true}
          titleIconVariant={"warning"}
          message={`Are you sure you want to delete this product?`}
          confirmBtnVariant={ButtonVariant.danger}
          confirmBtnLabel="Delete"
          cancelBtnLabel="Cancel"
          onCancel={() => setIsConfirmDialogOpen(false)}
          onClose={() => setIsConfirmDialogOpen(false)}
          onConfirm={() => {
            if (productIdToDelete) {
              deleteProduct(productIdToDelete);
              setProductIdToDelete(undefined);
            }
            setIsConfirmDialogOpen(false);
          }}
        />
      )}
    </>
  );
};
