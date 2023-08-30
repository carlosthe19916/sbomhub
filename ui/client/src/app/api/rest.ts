import axios from "axios";
import {
  Advisory,
  AdvisoryDetails,
  ApiPaginatedResult,
  ApiRequestParams,
  Package,
  PackageDetails,
  Product,
} from "./models";
import { serializeRequestParamsForApi } from "@app/shared/hooks/table-controls";

const API = "/api";

const PRODUCTS = API + "/products";

const ADVISORY = API + "/advisory";
const ADVISORY_SEARCH = API + "/advisory/search";

const PACKAGE = API + "/package";
const PACKAGE_SEARCH = API + "/package/search";

interface ApiSearchResult<T> {
  total: number;
  result: T[];
}

export const getApiPaginatedResult = <T>(
  url: string,
  params: ApiRequestParams = {}
): Promise<ApiPaginatedResult<T>> =>
  axios
    .get<ApiSearchResult<T>>(url, {
      params: serializeRequestParamsForApi(params),
    })
    .then(({ data }) => ({
      data: data.result,
      total: data.total,
      params,
    }));

export const getProducts = () => {
  return axios.get<Product[]>(PRODUCTS);
};

export const getProductById = (id: string) => {
  return axios.get<Product>(`${PRODUCTS}/${id}`);
};

export const createProduct = (obj: Product): Promise<Product> =>
  axios.post(PRODUCTS, obj);

export const updateProduct = (obj: Product): Promise<Product> =>
  axios.put(`${PRODUCTS}/${obj.name}`, obj);

export const deleteProduc = (id: string): Promise<Product> =>
  axios.delete(`${PRODUCTS}/${id}`);

export const getAdvisories = (params: ApiRequestParams = {}) => {
  return getApiPaginatedResult<Advisory>(ADVISORY_SEARCH, params);
};

export const getAdvisoryById = (id: string) => {
  return axios.get<AdvisoryDetails>(`${ADVISORY}?id=${id}`);
};

export const getpackages = (params: ApiRequestParams = {}) => {
  return getApiPaginatedResult<Package>(PACKAGE_SEARCH, params);
};

export const getPackageById = (id: string) => {
  return axios.get<PackageDetails>(`${PACKAGE}?id=${id}`);
};
