import axios from "axios";
import {
  Advisory,
  AdvisoryDetails,
  ApiPaginatedResult,
  ApiRequestParams,
  Package,
  PackageDetails,
} from "./models";
import { serializeRequestParamsForApi } from "@app/shared/hooks/table-controls";

const API_V1 = "/api/v1";

const ADVISORY = API_V1 + "/advisory";
const ADVISORY_SEARCH = API_V1 + "/advisory/search";

const PACKAGE = API_V1 + "/package";
const PACKAGE_SEARCH = API_V1 + "/package/search";

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
