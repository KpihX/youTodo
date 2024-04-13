import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, byteSize } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './categorie.reducer';

export const CategorieDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const categorieEntity = useAppSelector(state => state.categorie.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="categorieDetailsHeading">
          <Translate contentKey="youTodoApp.categorie.detail.title">Categorie</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{categorieEntity.id}</dd>
          <dt>
            <span id="nom">
              <Translate contentKey="youTodoApp.categorie.nom">Nom</Translate>
            </span>
          </dt>
          <dd>{categorieEntity.nom}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="youTodoApp.categorie.description">Description</Translate>
            </span>
          </dt>
          <dd>{categorieEntity.description}</dd>
        </dl>
        <Button tag={Link} to="/categorie" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/categorie/${categorieEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CategorieDetail;
