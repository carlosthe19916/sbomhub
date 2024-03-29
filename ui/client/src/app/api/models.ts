export interface ApiFilter {
  field: string;
  operator?: "=" | "!=" | "~" | ">" | ">=" | "<" | "<=";
  value:
    | string
    | number
    | {
        list: (string | number)[];
        operator?: "AND" | "OR";
      };
}

export interface ApiRequestParams {
  filters?: ApiFilter[];
  sort?: {
    field: string;
    direction: "asc" | "desc";
  };
  page?: {
    pageNumber: number; // 1-indexed
    itemsPerPage: number;
  };
}

export interface ApiPaginatedResult<T> {
  data: T[];
  total: number;
  params: ApiRequestParams;
}

//

export type AdvisorySeverity = "Low" | "Moderate" | "Important" | "Critical";

export interface Advisory {
  id: string;
  title: string;
  snippet: string;
  desc: string;
  date: string;
  cves: string[];
  cvss_max: number;
  href: string;
  severity: AdvisorySeverity;
}

export interface AdvisoryDetails {
  product_tree: ProductTree;
  vulnerabilities: Vulnerability[];
}

export interface ProductTree {
  branches: Branch[];
  relationships: Relationship[];
}

export interface Branch {
  category: string;
  name: string;
  branches?: Branch[];
}

export interface Relationship {
  category: string;
  product_reference: string;
  relates_to_product_reference: string;
  full_product_name: {
    name: string;
    product_id: string;
  };
}

export interface Vulnerability {
  title: string;
  cve: string;
  discovery_date: string;
  release_date: string;
  scores: Score[];
  cwe?: {
    id: string;
    name: string;
  };
  product_status: { [k: string]: string[] };
}

export interface Score {
  cvss_v3: CVSS_v3;
}

export type BaseSeverity = "NONE" | "LOW" | "MEDIUM" | "HIGH" | "CRITICAL";

export interface CVSS_v3 {
  attackComplexity: string;
  attackVector: string;
  availabilityImpact: string;
  baseScore: number;
  baseSeverity: BaseSeverity;
  confidentialityImpact: string;
  integrityImpact: string;
  privilegesRequired: string;
  scope: string;
  userInteraction: string;
  vectorString: string;
  version: string;
  products: string[];
}

// Product
export interface Product {
  name: string;
  description?: string;
}

// Package

export interface Package {
  id: string;
  name: string;
  version: string;
  supplier: string;
  created: string;
  dependencies: string[];
  advisories: string[];
  href: string;
}

export interface PackageDetails {
  name: string;
  SPDXID: string;
  documentNamespace: string;
  spdxVersion: string;
  dataLicense: string;
  creationInfo: {
    created: string;
    licenseListVersion: string;
    creators: string[];
  };
  packages: {
    name: string;
    versionInfo: string;
  }[];
}
