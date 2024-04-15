import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, byteSize, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './tache.reducer';

export const TacheDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const tacheEntity = useAppSelector(state => state.tache.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="tacheDetailsHeading">
          <Translate contentKey="youTodoApp.tache.detail.title">Tache</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{tacheEntity.id}</dd>
          <dt>
            <span id="titre">
              <Translate contentKey="youTodoApp.tache.titre">Titre</Translate>
            </span>
          </dt>
          <dd>{tacheEntity.titre}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="youTodoApp.tache.description">Description</Translate>
            </span>
          </dt>
          <dd>{tacheEntity.description}</dd>
          <dt>
            <span id="dateEcheance">
              <Translate contentKey="youTodoApp.tache.dateEcheance">Date Echeance</Translate>
            </span>
          </dt>
          <dd>
            {tacheEntity.dateEcheance ? <TextFormat value={tacheEntity.dateEcheance} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="priorite">
              <Translate contentKey="youTodoApp.tache.priorite">Priorite</Translate>
            </span>
          </dt>
          <dd>{tacheEntity.priorite}</dd>
          <dt>
            <span id="statut">
              <Translate contentKey="youTodoApp.tache.statut">Statut</Translate>
            </span>
          </dt>
          <dd>{tacheEntity.statut}</dd>
          <dt>
            <Translate contentKey="youTodoApp.tache.categorie">Categorie</Translate>
          </dt>
          <dd>{tacheEntity.categorie ? tacheEntity.categorie.nom : 'Aucune Catégorie'}</dd>
          {/* <dt>
            <Translate contentKey="youTodoApp.tache.user">User</Translate>
          </dt>
          <dd>{tacheEntity.user ? tacheEntity.user.login : ''}</dd> */}
        </dl>
        <Button tag={Link} to="/tache" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/tache/${tacheEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default TacheDetail;
