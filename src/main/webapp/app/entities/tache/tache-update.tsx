import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { ICategorie } from 'app/shared/model/categorie.model';
import { getEntities as getCategories } from 'app/entities/categorie/categorie.reducer';
import { IUser } from 'app/shared/model/user.model';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { ITache } from 'app/shared/model/tache.model';
import { Priorite } from 'app/shared/model/enumerations/priorite.model';
import { StatutTache } from 'app/shared/model/enumerations/statut-tache.model';
import { getEntity, updateEntity, createEntity, reset } from './tache.reducer';

export const TacheUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const categories = useAppSelector(state => state.categorie.entities);
  const users = useAppSelector(state => state.userManagement.users);
  const tacheEntity = useAppSelector(state => state.tache.entity);
  const loading = useAppSelector(state => state.tache.loading);
  const updating = useAppSelector(state => state.tache.updating);
  const updateSuccess = useAppSelector(state => state.tache.updateSuccess);
  const prioriteValues = Object.keys(Priorite);
  const statutTacheValues = Object.keys(StatutTache);

  const handleClose = () => {
    navigate('/tache');
  };

  useEffect(() => {
    if (!isNew) {
      dispatch(getEntity(id));
    }

    dispatch(getCategories({}));
    dispatch(getUsers({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  // eslint-disable-next-line complexity
  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }

    const entity = {
      ...tacheEntity,
      ...values,
      categorie: categories.find(it => it.id.toString() === values.categorie?.toString()),
      user: users.find(it => it.id.toString() === values.user?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          priorite: 'BASSE',
          statut: 'OUVERT',
          ...tacheEntity,
          categorie: tacheEntity?.categorie?.id,
          user: tacheEntity?.user?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="youTodoApp.tache.home.createOrEditLabel" data-cy="TacheCreateUpdateHeading">
            <Translate contentKey="youTodoApp.tache.home.createOrEditLabel">Create or edit a Tache</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="tache-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('youTodoApp.tache.titre')}
                id="tache-titre"
                name="titre"
                data-cy="titre"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  minLength: { value: 3, message: translate('entity.validation.minlength', { min: 3 }) },
                  maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) },
                }}
              />
              <ValidatedField
                label={translate('youTodoApp.tache.description')}
                id="tache-description"
                name="description"
                data-cy="description"
                type="textarea"
              />
              <ValidatedField
                label={translate('youTodoApp.tache.dateEcheance')}
                id="tache-dateEcheance"
                name="dateEcheance"
                data-cy="dateEcheance"
                type="date"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('youTodoApp.tache.priorite')}
                id="tache-priorite"
                name="priorite"
                data-cy="priorite"
                type="select"
              >
                {prioriteValues.map(priorite => (
                  <option value={priorite} key={priorite}>
                    {translate('youTodoApp.Priorite.' + priorite)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField label={translate('youTodoApp.tache.statut')} id="tache-statut" name="statut" data-cy="statut" type="select">
                {statutTacheValues.map(statutTache => (
                  <option value={statutTache} key={statutTache}>
                    {translate('youTodoApp.StatutTache.' + statutTache)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                id="tache-categorie"
                name="categorie"
                data-cy="categorie"
                label={translate('youTodoApp.tache.categorie')}
                type="select"
              >
                <option value="" key="0" />
                {categories
                  ? categories.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.nom}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="tache-user" name="user" data-cy="user" label={translate('youTodoApp.tache.user')} type="select">
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.login}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/tache" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default TacheUpdate;
