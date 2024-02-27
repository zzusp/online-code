import { createHashRouter, Navigate, RouterProvider, useParams } from "react-router-dom";
import ReactDOM from "react-dom";
import React from "react";

import Pages from "./pages";
import ProcessDesign from "./ProcessDesign";

const router = createHashRouter([
  {
    path: '/pages/:page',
    element: <Pages />
  },
  {
    path: '/process/design/:id',
    element: <ProcessDesign />
  },
  {
    path: '/',
    element: <Navigate to="/pages/login" replace={true} />
  }
]);

ReactDOM.render(<RouterProvider router={router} />, document.getElementById('ice-container'));
