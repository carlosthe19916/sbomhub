import React, { Suspense, lazy } from "react";
import { Navigate, useRoutes } from "react-router-dom";

import { Bullseye, Spinner } from "@patternfly/react-core";

const Home = lazy(() => import("./pages/home"));
const Products = lazy(() => import("./pages/products"));
const Advisories = lazy(() => import("./pages/advisories"));
const Packages = lazy(() => import("./pages/packages"));
const ViewPackage = lazy(() => import("./pages/view-package"));

export const ViewPackageRouteParam = "packageId";

export const AppRoutes = () => {
  const allRoutes = useRoutes([
    { path: "/", element: <Products /> },
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
