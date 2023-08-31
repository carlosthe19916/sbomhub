import React, { Suspense, lazy } from "react";
import { Navigate, useRoutes } from "react-router-dom";

import { Bullseye, Spinner } from "@patternfly/react-core";

const Home = lazy(() => import("./pages/home"));
const Products = lazy(() => import("./pages/products"));
const ViewProduct = lazy(() => import("./pages/view-product"));
const ViewSbom = lazy(() => import("./pages/view-sbom"));
const Advisories = lazy(() => import("./pages/advisories"));
const Packages = lazy(() => import("./pages/packages"));
const ViewPackage = lazy(() => import("./pages/view-package"));

export const ViewProductRouteParam = "productId";
export const ViewPackageRouteParam = "packageId";

export const AppRoutes = () => {
  const allRoutes = useRoutes([
    { path: "/", element: <Products /> },
    { path: "/products", element: <Packages /> },
    { path: `/products/:${ViewProductRouteParam}`, element: <ViewProduct /> },
    { path: `/products/:${ViewProductRouteParam}/sboms/:sbombId`, element: <ViewSbom /> },
    { path: "/home", element: <Home /> },
    { path: "/advisory", element: <Advisories /> },
    { path: "/package", element: <Packages /> },
    { path: `/package/:${ViewPackageRouteParam}`, element: <ViewPackage /> },
    { path: "*", element: <Navigate to="/" /> },
  ]);

  return (
    <Suspense
      fallback={
        <Bullseye>
          <Spinner />
        </Bullseye>
      }
    >
      {allRoutes}
    </Suspense>
  );
};
