import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Tache from './tache';
import TacheDetail from './tache-detail';
import TacheUpdate from './tache-update';
import TacheDeleteDialog from './tache-delete-dialog';

const TacheRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Tache />} />
    <Route path="new" element={<TacheUpdate />} />
    <Route path=":id">
      <Route index element={<TacheDetail />} />
      <Route path="edit" element={<TacheUpdate />} />
      <Route path="delete" element={<TacheDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TacheRoutes;
