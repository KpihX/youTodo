import React, { useState, useEffect } from 'react';
import InfiniteScroll from 'react-infinite-scroll-component';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table, Card, CardBody } from 'reactstrap';
import { byteSize, Translate, TextFormat, getPaginationState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortUp, faSortDown } from '@fortawesome/free-solid-svg-icons';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { faEye, faPencilAlt, faTrash } from '@fortawesome/free-solid-svg-icons';

import { getEntities, reset } from './tache.reducer';

const getColorByPriority = priorite => {
  switch (priorite) {
    case 'BASSE':
      return 'bg-info';
    case 'MOYENNE':
      return 'bg-warning';
    case 'HAUTE':
      return 'bg-danger';
    default:
      return 'bg-secondary';
  }
};
// const sortByPriority = (list) => {
//   const priorityOrder = ['HAUTE', 'MOYENNE', 'BASSE', 'AUTRE']; // Définir l'ordre de priorité
//   return list.sort((a, b) => {
//     return priorityOrder.indexOf(a.priorite) - priorityOrder.indexOf(b.priorite);
//   });
// };

export const Tache = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );
  const [sorting, setSorting] = useState(false);

  const account = useAppSelector(state => state.authentication.account);
  const tacheList = useAppSelector(state => state.tache.entities);
  const loading = useAppSelector(state => state.tache.loading);
  const links = useAppSelector(state => state.tache.links);
  const updateSuccess = useAppSelector(state => state.tache.updateSuccess);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        user_id: account.id,
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  };

  const resetAll = () => {
    dispatch(reset());
    setPaginationState({
      ...paginationState,
      activePage: 1,
    });
    dispatch(
      getEntities({
        user_id: account.id,
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  };

  useEffect(() => {
    resetAll();
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      resetAll();
    }
  }, [updateSuccess]);

  useEffect(() => {
    getAllEntities();
  }, [paginationState.activePage]);

  const handleLoadMore = () => {
    if ((window as any).pageYOffset > 0) {
      setPaginationState({
        ...paginationState,
        activePage: paginationState.activePage + 1,
      });
    }
  };

  useEffect(() => {
    if (sorting) {
      getAllEntities();
      setSorting(false);
    }
  }, [sorting]);

  const isDatePassed = date => {
    const currentDate = new Date();
    return currentDate > new Date(date);
  };

  const sort = p => () => {
    dispatch(reset());
    setPaginationState({
      ...paginationState,
      activePage: 1,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
    setSorting(true);
  };

  const handleSyncList = () => {
    resetAll();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = paginationState.sort;
    const order = paginationState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    } else {
      return order === ASC ? faSortUp : faSortDown;
    }
  };

  const sortByPriority = list => {
    const priorityOrder = ['HAUTE', 'MOYENNE', 'BASSE'];
    return list.slice().sort((a, b) => {
      return priorityOrder.indexOf(a.priorite) - priorityOrder.indexOf(b.priorite);
    });
  };
  const sortByPriorityAndDate = list => {
    const priorityOrder = ['HAUTE', 'MOYENNE', 'BASSE'];
    const currentDate = new Date();

    return list.slice().sort((a, b) => {
      const priorityComparison = priorityOrder.indexOf(a.priorite) - priorityOrder.indexOf(b.priorite);

      if (priorityComparison !== 0) {
        return priorityComparison;
      } else {
        const isDatePassedA = new Date(a.dateEcheance) < currentDate;
        const isDatePassedB = new Date(b.dateEcheance) < currentDate;

        if (isDatePassedA && !isDatePassedB) {
          return 1;
        } else if (!isDatePassedA && isDatePassedB) {
          return -1;
        } else {
          return 0;
        }
      }
    });
  };

  // return (
  //   <div>
  //     <h2 id="tache-heading" data-cy="TacheHeading">
  //       <Translate contentKey="youTodoApp.tache.home.title">Taches</Translate>
  //       <div className="d-flex justify-content-end">
  //         <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
  //           <FontAwesomeIcon icon="sync" spin={loading} />{' '}
  //           <Translate contentKey="youTodoApp.tache.home.refreshListLabel">Refresh List</Translate>
  //         </Button>
  //         <Link to="/tache/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
  //           <FontAwesomeIcon icon="plus" />
  //           &nbsp;
  //           <Translate contentKey="youTodoApp.tache.home.createLabel">Create new Tache</Translate>
  //         </Link>
  //       </div>
  //     </h2>
  //     <div className="table-responsive">
  //       <InfiniteScroll
  //         dataLength={tacheList ? tacheList.length : 0}
  //         next={handleLoadMore}
  //         hasMore={paginationState.activePage - 1 < links.next}
  //         loader={<div className="loader">Loading ...</div>}
  //       >
  //         {tacheList && tacheList.length > 0 ? (
  //           <Table responsive>
  //             <thead>
  //               <tr>
  //                 <th className="hand" onClick={sort('id')}>
  //                   <Translate contentKey="youTodoApp.tache.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
  //                 </th>
  //                 <th className="hand" onClick={sort('titre')}>
  //                   <Translate contentKey="youTodoApp.tache.titre">Titre</Translate>{' '}
  //                   <FontAwesomeIcon icon={getSortIconByFieldName('titre')} />
  //                 </th>
  //                 <th className="hand" onClick={sort('description')}>
  //                   <Translate contentKey="youTodoApp.tache.description">Description</Translate>{' '}
  //                   <FontAwesomeIcon icon={getSortIconByFieldName('description')} />
  //                 </th>
  //                 <th className="hand" onClick={sort('dateEcheance')}>
  //                   <Translate contentKey="youTodoApp.tache.dateEcheance">Date Echeance</Translate>{' '}
  //                   <FontAwesomeIcon icon={getSortIconByFieldName('dateEcheance')} />
  //                 </th>
  //                 <th className="hand" onClick={sort('priorite')}>
  //                   <Translate contentKey="youTodoApp.tache.priorite">Priorite</Translate>{' '}
  //                   <FontAwesomeIcon icon={getSortIconByFieldName('priorite')} />
  //                 </th>
  //                 <th className="hand" onClick={sort('statut')}>
  //                   <Translate contentKey="youTodoApp.tache.statut">Statut</Translate>{' '}
  //                   <FontAwesomeIcon icon={getSortIconByFieldName('statut')} />
  //                 </th>
  //                 <th>
  //                   <Translate contentKey="youTodoApp.tache.categorie">Categorie</Translate> <FontAwesomeIcon icon="sort" />
  //                 </th>
  //                 {/* <th>
  //                   <Translate contentKey="youTodoApp.tache.user">User</Translate> <FontAwesomeIcon icon="sort" />
  //                 </th> */}
  //                 <th />
  //               </tr>
  //             </thead>
  //             <tbody>
  //               {tacheList
  //                 ? tacheList.map((tache, i) => (
  //                     <tr key={`entity-${i}`} data-cy="entityTable">
  //                       <td>
  //                         <Button tag={Link} to={`/tache/${tache.id}`} color="link" size="sm">
  //                           {tache.id}
  //                         </Button>
  //                       </td>
  //                       <td>{tache.titre}</td>
  //                       <td>{tache.description}</td>
  //                       <td>
  //                         {tache.dateEcheance ? <TextFormat type="date" value={tache.dateEcheance} format={APP_LOCAL_DATE_FORMAT} /> : null}
  //                       </td>
  //                       <td>
  //                         <Translate contentKey={`youTodoApp.Priorite.${tache.priorite}`} />
  //                       </td>
  //                       <td>
  //                         <Translate contentKey={`youTodoApp.StatutTache.${tache.statut}`} />
  //                       </td>
  //                       <td>{tache.categorie ? <Link to={`/categorie/${tache.categorie.id}`}>{tache.categorie.nom}</Link> : ''}</td>
  //                       {/* <td>{tache.user ? tache.user.login : ''}</td> */}
  //                       <td className="text-end">
  //                         <div className="btn-group flex-btn-group-container">
  //                           <Button tag={Link} to={`/tache/${tache.id}`} color="info" size="sm" data-cy="entityDetailsButton">
  //                             <FontAwesomeIcon icon="eye" />{' '}
  //                             <span className="d-none d-md-inline">
  //                               <Translate contentKey="entity.action.view">View</Translate>
  //                             </span>
  //                           </Button>
  //                           <Button tag={Link} to={`/tache/${tache.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
  //                             <FontAwesomeIcon icon="pencil-alt" />{' '}
  //                             <span className="d-none d-md-inline">
  //                               <Translate contentKey="entity.action.edit">Edit</Translate>
  //                             </span>
  //                           </Button>
  //                           <Button
  //                             onClick={() => (window.location.href = `/tache/${tache.id}/delete`)}
  //                             color="danger"
  //                             size="sm"
  //                             data-cy="entityDeleteButton"
  //                           >
  //                             <FontAwesomeIcon icon="trash" />{' '}
  //                             <span className="d-none d-md-inline">
  //                               <Translate contentKey="entity.action.delete">Delete</Translate>
  //                             </span>
  //                           </Button>
  //                         </div>
  //                       </td>
  //                     </tr>
  //                   ))
  //                 : null}
  //             </tbody>
  //           </Table>
  //         ) : (
  //           !loading && (
  //             <div className="alert alert-warning">
  //               <Translate contentKey="youTodoApp.tache.home.notFound">No Taches found</Translate>
  //             </div>
  //           )
  //         )}
  //       </InfiniteScroll>
  //     </div>
  //   </div>
  // );

  return (
    <div>
      <h2 id="tache-heading" data-cy="TacheHeading">
        Listes Des Taches
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="youTodoApp.tache.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/tache/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="youTodoApp.tache.home.createLabel">Create new Tache</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        <InfiniteScroll
          dataLength={tacheList ? tacheList.length : 0}
          next={handleLoadMore}
          hasMore={paginationState.activePage - 1 < links.next}
          loader={<div className="loader">Loading ...</div>}
        >
          {tacheList && tacheList.length > 0 ? (
            <div>
              <div className="container rad">
                {sortByPriorityAndDate(tacheList).map((tache, index) => (
                  <div className={`row mb-3 rad rounded p-3 ${getColorByPriority(tache.priorite)}`} key={`entity-${index}`}>
                    <div className="col-md-10 rad">
                      <Card className="bg-gray rad">
                        <CardBody>
                          <h5>
                            <Link to={`/tache/${tache.id}`} className="text-primary">
                              {tache.titre}
                            </Link>
                          </h5>
                          <p className="text-muted">{tache.description}</p>
                          <p>
                            <strong>Date Échéance:</strong>{' '}
                            {isDatePassed(tache.dateEcheance) ? (
                              <span className="text-danger">Date déjà passée</span>
                            ) : (
                              <TextFormat type="date" value={tache.dateEcheance} format={APP_LOCAL_DATE_FORMAT} />
                            )}
                          </p>
                          <p>
                            <strong>Priorité:</strong> <Translate contentKey={`youTodoApp.Priority.${tache.priorite}`} />
                          </p>
                          <p>
                            <strong>Statut:</strong> <Translate contentKey={`youTodoApp.StatutTache.${tache.statut}`} />
                          </p>
                          {tache.categorie && (
                            <p>
                              <strong>Catégorie:</strong>{' '}
                              <Link to={`/categorie/${tache.categorie.id}`} className="text-info">
                                {tache.categorie.nom}
                              </Link>
                            </p>
                          )}
                        </CardBody>
                      </Card>
                    </div>
                    <div className="col-md-2 d-flex flex-column justify-content-between">
                      <div>
                        <Button
                          tag={Link}
                          to={`/tache/${tache.id}`}
                          size="sm"
                          className={`me-3 marge ${getColorByPriority(tache.priorite)}`}
                        >
                          <FontAwesomeIcon icon={faEye} />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.view">View</Translate>
                          </span>
                        </Button>
                        <Button
                          tag={Link}
                          to={`/tache/${tache.id}/edit`}
                          size="sm"
                          className={`me-3 marge ${getColorByPriority(tache.priorite)}`}
                        >
                          <FontAwesomeIcon icon={faPencilAlt} />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.edit">Edit</Translate>
                          </span>
                        </Button>
                        <Button
                          onClick={() => {
                            if (window.confirm('Are you sure you want to delete this item?')) {
                              window.location.href = `/tache/${tache.id}/delete`;
                            }
                          }}
                          color="danger"
                          size="sm"
                          className={`me-3 marge_ ${getColorByPriority(tache.priorite)}`}
                        >
                          <FontAwesomeIcon icon={faTrash} />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.delete">Delete</Translate>
                          </span>
                        </Button>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
              {/* <Table responsive>
              <thead>
                <tr>
                  <th className="hand" onClick={sort('id')}>
                    <Translate contentKey="youTodoApp.tache.id">ID</Translate> <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                  </th>
                  <th className="hand" onClick={sort('titre')}>
                    <Translate contentKey="youTodoApp.tache.titre">Titre</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('titre')} />
                  </th>
                  <th className="hand" onClick={sort('description')}>
                    <Translate contentKey="youTodoApp.tache.description">Description</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('description')} />
                  </th>
                  <th className="hand" onClick={sort('dateEcheance')}>
                    <Translate contentKey="youTodoApp.tache.dateEcheance">Date Echeance</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('dateEcheance')} />
                  </th>
                  <th className="hand" onClick={sort('priorite')}>
                    <Translate contentKey="youTodoApp.tache.priorite">Priorite</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('priorite')} />
                  </th>
                  <th className="hand" onClick={sort('statut')}>
                    <Translate contentKey="youTodoApp.tache.statut">Statut</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('statut')} />
                  </th>
                  <th>
                    <Translate contentKey="youTodoApp.tache.categorie">Categorie</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {tacheList.map((tache, i) => (
                  <tr key={`entity-${i}`} data-cy="entityTable">
                    <td>
                      <Button tag={Link} to={`/tache/${tache.id}`} color="link" size="sm">
                        {tache.id}
                      </Button>
                    </td>
                    <td>{tache.titre}</td>
                    <td>{tache.description}</td>
                    <td>
                      {tache.dateEcheance ? <TextFormat type="date" value={tache.dateEcheance} format={APP_LOCAL_DATE_FORMAT} /> : null}
                    </td>
                    <td>
                      <Translate contentKey={`youTodoApp.Priority.${tache.priorite}`} />
                    </td>
                    <td>
                      <Translate contentKey={`youTodoApp.StatutTache.${tache.statut}`} />
                    </td>
                    <td>{tache.categorie ? <Link to={`/categorie/${tache.categorie.id}`}>{tache.categorie.nom}</Link> : ''}</td>
                    <td className="text-end">
                      <div className="btn-group flex-btn-group-container">
                        <Button tag={Link} to={`/tache/${tache.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                          <FontAwesomeIcon icon="eye" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.view">View</Translate>
                          </span>
                        </Button>
                        <Button tag={Link} to={`/tache/${tache.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                          <FontAwesomeIcon icon="pencil-alt" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.edit">Edit</Translate>
                          </span>
                        </Button>
                        <Button
                          onClick={() => (window.location.href = `/tache/${tache.id}/delete`)}
                          color="danger"
                          size="sm"
                          data-cy="entityDeleteButton"
                        >
                          <FontAwesomeIcon icon="trash" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.delete">Delete</Translate>
                          </span>
                        </Button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table> */}
            </div>
          ) : (
            !loading && (
              <div className="alert alert-warning">
                <Translate contentKey="youTodoApp.tache.home.notFound">No Taches found</Translate>
              </div>
            )
          )}
        </InfiniteScroll>
      </div>
    </div>
  );
};

export default Tache;
