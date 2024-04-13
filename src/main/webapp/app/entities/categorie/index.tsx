import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Categorie from './categorie';
import CategorieDetail from './categorie-detail';
import CategorieUpdate from './categorie-update';
import CategorieDeleteDialog from './categorie-delete-dialog';

const CategorieRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Categorie />} />
    <Route path="new" element={<CategorieUpdate />} />
    <Route path=":id">
      <Route index element={<CategorieDetail />} />
      <Route path="edit" element={<CategorieUpdate />} />
      <Route path="delete" element={<CategorieDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CategorieRoutes;
