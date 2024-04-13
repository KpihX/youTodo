import React, { useState, useEffect } from 'react';
import InfiniteScroll from 'react-infinite-scroll-component';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { byteSize, Translate, TextFormat, getPaginationState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortUp, faSortDown } from '@fortawesome/free-solid-svg-icons';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities, reset } from './tache.reducer';

export const Tache = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );
  const [sorting, setSorting] = useState(false);

  const tacheList = useAppSelector(state => state.tache.entities);
  const loading = useAppSelector(state => state.tache.loading);
  const links = useAppSelector(state => state.tache.links);
  const updateSuccess = useAppSelector(state => state.tache.updateSuccess);

  const getAllEntities = () => {
    dispatch(
      getEntities({
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
    dispatch(getEntities({}));
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

  return (
    <div>
      <h2 id="tache-heading" data-cy="TacheHeading">
        <Translate contentKey="youTodoApp.tache.home.title">Taches</Translate>
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
            <Table responsive>
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
                  <th>
                    <Translate contentKey="youTodoApp.tache.user">User</Translate> <FontAwesomeIcon icon="sort" />
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
                      <Translate contentKey={`youTodoApp.Priorite.${tache.priorite}`} />
                    </td>
                    <td>
                      <Translate contentKey={`youTodoApp.StatutTache.${tache.statut}`} />
                    </td>
                    <td>{tache.categorie ? <Link to={`/categorie/${tache.categorie.id}`}>{tache.categorie.nom}</Link> : ''}</td>
                    <td>{tache.user ? tache.user.login : ''}</td>
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
            </Table>
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