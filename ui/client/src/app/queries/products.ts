import { AxiosError } from "axios";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import { Product } from "@app/api/models";
import {
  createProduct,
  deleteProduc,
  getProductById,
  getProducts,
  updateProduct,
} from "@app/api/rest";

export interface IProductsFetchState {
  result: Product[];
  isFetching: boolean;
  fetchError: unknown;
  refetch: () => void;
}

export const ProductsQueryKey = "products";

export const useFetchProducts = () => {
  const { data, isLoading, error, refetch } = useQuery({
    queryKey: [ProductsQueryKey],
    // queryFn: async () => await getProducts(),
    queryFn: () => ({
      data: [
        {
          name: "JBoss EAP",
          description: "Red Hat JBoss EAP application server",
        },
        {
          name: "Ansible",
          description: "Ansible automation platform",
        },
        {
          name: "Openshift",
          description: "Kubernetes enterprise platform",
        },
        {
          name: "OpenJDK",
          description:
            "Free and open-source implementation of the Java Platform",
        },
        {
          name: "Quarkus",
          description: "Java framework tailored for deployment on Kubernetes.",
        },
      ] as Product[],
    }),
    onError: (error) => console.log("error, ", error),
    keepPreviousData: true,
  });
  return {
    result: data?.data || [],
    isFetching: isLoading,
    fetchError: error,
    refetch,
  };
};

export const useProductById = (id: string) => {
  const { data, isLoading, error } = useQuery(
    [ProductsQueryKey, id],
    async () => (await getProductById(id)).data,
    { onError: (error) => console.log(error) }
  );

  return {
    result: data,
    isFetching: isLoading,
    fetchError: error as AxiosError,
  };
};

export const useCreateProductMutation = (
  onSuccess: (res: any) => void,
  onError: (err: AxiosError) => void
) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: createProduct,
    onSuccess: (res) => {
      onSuccess(res);
      queryClient.invalidateQueries([ProductsQueryKey]);
    },
    onError,
  });
};

export const useUpdateProductMutation = (
  onSuccess: (res: any) => void,
  onError: (err: AxiosError) => void
) => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: updateProduct,
    onSuccess: (res) => {
      onSuccess(res);
      queryClient.invalidateQueries([ProductsQueryKey]);
    },
    onError: onError,
  });
};

export const useDeleteProductMutation = (
  onSuccess: (res: any) => void,
  onError: (err: AxiosError) => void
) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: deleteProduc,
    onSuccess: (res) => {
      onSuccess(res);
      queryClient.invalidateQueries([ProductsQueryKey]);
    },
    onError: onError,
  });
};
