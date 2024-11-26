import { createHashRouter, Navigate, RouterProvider, useParams } from "react-router-dom";
import ReactDOM from "react-dom";
import React from "react";

import Layout from "./layout";
import Pages from "./pages";
import ProcessDesign from "./process-design";
import Icons from "./icons";

const router = createHashRouter([
  {
    path: '/',
    element: <Layout />,
    children: [
      {
        path: 'pages/:page',
        element: <Pages />
      },{
        path: 'icons',
        element: <Icons />
      }
    ]
  },
  {
    path: '/pages/login',
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
