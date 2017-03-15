package com.yukthi.persistence;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.function.Function;

import javax.persistence.CascadeType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.apache.commons.lang.StringUtils;

import com.yukthi.persistence.annotations.DataType;
import com.yukthi.persistence.annotations.DeleteWithParent;
import com.yukthi.persistence.annotations.ForeignConstraintMessage;

public class ForeignConstraintDetails
{
	public static final String FOREIGN_CONSTRAINT_PREFIX = "FK_";
	
	/**
	 * Field name of target entity which maintains this relation (foreign key).
	 * And also indicates this entity does not maintain any direct relation
	 */
	private String mappedBy;
	
	/**
	 * Indicates whether child entities should be removed when parent is removed
	 * (controlled by delete cascade attribute while creating table). Default is false
	 */
	private boolean isDeleteCascaded = false;
	
	/**
	 * Should (and will be considered) on fields holding inverse/mapped relation. And if specified, with save of current entity
	 * the child entity will also get persisted. Default is false
	 */
	private boolean isSaveCascaded = false;

	/**
	 * Provides details of the joining table, required for many-to-many relation
	 */
	private JoinTableDetails joinTableDetails;

	/**
	 * Defines the relation type
	 */
	private RelationType relationType;

	/**
	 * Target entity to which relation is targeted
	 */
	private EntityDetails targetEntityDetails;

	/**
	 * Field on which this relation end is defined
	 */
	private Field ownerField;

	/**
	 * Entity details in which this relation end is defined
	 */
	private EntityDetails ownerEntityDetails;
	
	/**
	 * Message to be used when constraint fails
	 */
	private String message;
	
	/**
	 * Message to be used when parent entity is being deleted without deleting 
	 * child items 
	 */
	private String deleteMessage;
	
	private ForeignConstraintDetails(String mappedBy, RelationType relationType, Field ownerField)
	{
		this.mappedBy = mappedBy;
		this.relationType = relationType;
		this.ownerField = ownerField;
		
		ForeignConstraintMessage constraintMessage = ownerField.getAnnotation(ForeignConstraintMessage.class);
		
		if(constraintMessage != null)
		{
			this.message = constraintMessage.message();
			this.deleteMessage = constraintMessage.deleteMessage();
		}
	}
	
	/**
	 * Creates simple foreign key relationship
	 * 
	 * @param targetEntityDetails
	 * @param ownerField
	 * @param ownerEntityDetails
	 */
	public ForeignConstraintDetails(EntityDetails targetEntityDetails, Field ownerField, EntityDetails ownerEntityDetails)
	{
		this.isDeleteCascaded = true;
		this.relationType = RelationType.ONE_TO_ONE;
		
		this.targetEntityDetails = targetEntityDetails;
		this.ownerField = ownerField;
		this.ownerEntityDetails = ownerEntityDetails;

		ForeignConstraintMessage constraintMessage = ownerField.getAnnotation(ForeignConstraintMessage.class);
		
		if(constraintMessage != null)
		{
			this.message = constraintMessage.message();
			this.deleteMessage = constraintMessage.deleteMessage();
		}
	}

	/**
	 * Fetches the constraint message to be used when constraint fails
	 * @return the {@link #message message}
	 */
	public String getMessage()
	{
		return message;
	}
	
	/**
	 * Message to be used when parent entity is being deleted without deleting 
	 * child items 
	 * @return the {@link #deleteMessage deleteMessage}
	 */
	public String getDeleteMessage()
	{
		return deleteMessage;
	}

	/**
	 * Gets the preferred name of this constraint
	 * @return
	 */
	public String getConstraintName()
	{
		String ownerCol = ownerEntityDetails.getFieldDetailsByField(ownerField.getName()).getDbColumnName().toUpperCase();
		return FOREIGN_CONSTRAINT_PREFIX + ownerEntityDetails.getTableName().toUpperCase() + "_" + ownerCol;
	}

	/**
	 * Checks if DELETE cascade is specified in given options
	 * 
	 * @param cascadeOptions
	 * @return
	 */
	private static boolean isCascaded(CascadeType cascadeOptions[], CascadeType target)
	{
		// if no cascade options are specified
		if(cascadeOptions == null)
		{
			// assume child entities should not be removed with parent
			return false;
		}

		// loop through specified options
		for(CascadeType type : cascadeOptions)
		{
			// if remove is specified
			if(type == target)
			{
				// assume child needs to be deleted with parent
				return true;
			}
		}

		// if remove is not specified, assume child should not be deleted with
		// parent
		return false;
	}

	/**
	 * Fetches the field type. If collection type is expected, then fetches the
	 * element type. Also ensures collection is specified when expected and also
	 * vice-versa.
	 * 
	 * @param entityType
	 * @param field
	 * @param collectionExpected
	 * @param mappingFieldName
	 * @return
	 */
	private static Class<?> getFieldType(Class<?> entityType, Field field, boolean collectionExpected, String mappingFieldName)
	{
		Class<?> fieldType = field.getType();

		// if collection is not expected
		if(!collectionExpected)
		{
			// but if field is collection type
			if(Collection.class.isAssignableFrom(field.getType()))
			{
				// if this is source field
				if(mappingFieldName == null)
				{
					throw new InvalidMappingException(String.format("Found collection field when as per entity-relation collection is not expected. Field '%s.%s'", entityType.getName(), field.getName()));
				}
				// if this is target field (relation other end)
				else
				{
					throw new InvalidMappingException(String.format("Found collection field when as per entity-relation collection is not expected. Field '%s.%s' mapping used by '%s'", entityType.getName(), field.getName(), mappingFieldName));
				}
			}

			return fieldType;
		}

		// if the field is not of collection type
		if(!Collection.class.isAssignableFrom(field.getType()))
		{
			// if this is source field
			if(mappingFieldName == null)
			{
				throw new InvalidMappingException(String.format("Found non-collection field when as per entity-relation collection is expected. Field '%1$s.%2$s'", entityType.getName(), field.getName()));
			}
			// if this is target field (relation other end)
			else
			{
				throw new InvalidMappingException(String.format("Found non-collection field when as per entity-relation collection is expected. Field '%1$s.%2$s' mapping used by '%3$s'", entityType.getName(), field.getName(), mappingFieldName));
			}
		}

		Type genericType = field.getGenericType();

		// if raw field type is used or type-variables are used for collection
		// declaration
		if(!(genericType instanceof ParameterizedType) || !(((ParameterizedType) genericType).getActualTypeArguments()[0] instanceof Class))
		{
			// if this is source field
			if(mappingFieldName == null)
			{
				throw new InvalidMappingException(String.format("For relation processing generic collection is expected without type-variables. Field '%1$s.%2$s'", entityType.getName(), field.getName()));
			}
			// if this is target field (relation other end)
			else
			{
				throw new InvalidMappingException(String.format("For relation processing generic collection is expected without type-variables. Field '%1$s.%2$s' mapping used by '%3$s'", entityType.getName(), field.getName(), mappingFieldName));
			}
		}

		fieldType = (Class<?>) ((ParameterizedType) genericType).getActualTypeArguments()[0];

		return fieldType;
	}

	/**
	 * Fetches the join table details if any. Fields with mapped relation should
	 * not have {@link JoinTable} annotation. For one-to-many and many-to-many
	 * mappedBy or join-table info is mandatory.
	 * 
	 * @param sourceEntityDetails
	 * @param sourceField
	 * @param mappedBy
	 * @param relationType
	 * @param targetEntityDetails
	 * @return
	 */
	private static JoinTableDetails getJoinTableDetails(EntityDetails sourceEntityDetails, Field sourceField, String mappedBy, RelationType relationType, EntityDetails targetEntityDetails)
	{
		// get join table details
		JoinTable joinTable = sourceField.getAnnotation(JoinTable.class);

		// if @JoinTable is specified on mapped field throw error
		if(StringUtils.isNotEmpty(mappedBy) && joinTable != null)
		{
			throw new InvalidMappingException(String.format("'@JoinTable' is specified on relation-mapped field '%1$s.%2$s' ", sourceEntityDetails.getEntityType().getName(), sourceField.getName()));
		}

		// if join-table is also not specified
		if(joinTable == null)
		{
			// if relation is one-to-many or many-to-many and mappedBy is not
			// specified
			if(relationType.isCollectionExpected() && StringUtils.isEmpty(mappedBy))
			{
				throw new InvalidMappingException(String.format("Both 'mappedBy' and '@JoinTable' is missing for %3$s relation for field '%1$s.%2$s' ", sourceEntityDetails.getEntityType().getName(), sourceField.getName(), relationType));
			}

			return null;
		}

		// get join table column names if specified
		String joinColumn = (joinTable.joinColumns().length > 0) ? joinTable.joinColumns()[0].name() : null;
		String inverseJoinColumn = (joinTable.inverseJoinColumns().length > 0) ? joinTable.inverseJoinColumns()[0].name() : null;

		if(joinColumn == null)
		{
			joinColumn = sourceEntityDetails.getTableName() + "_ID";
		}

		if(inverseJoinColumn == null)
		{
			inverseJoinColumn = targetEntityDetails.getTableName() + "_ID";
		}
		
		DataType sourceDataType = DataType.getDataType(sourceEntityDetails.getIdField().getField().getType());
		DataType targetDataType = DataType.getDataType(targetEntityDetails.getIdField().getField().getType());

		return new JoinTableDetails(joinTable.name(), joinColumn, sourceDataType, inverseJoinColumn, targetDataType, sourceEntityDetails, targetEntityDetails);
	}

	/**
	 * Fetches foreign constrain details for specified details. And also
	 * validates the field types and mappings
	 * 
	 * @param mappedBy
	 * @param delCascaded
	 * @param relationType
	 * @param sourceEntityDetails
	 * @param sourceField
	 * @param entityDetailsProvider
	 * @return
	 */
	private static ForeignConstraintDetails getForeignConstraint(String mappedBy, CascadeType cascadedTypes[], RelationType relationType, EntityDetails sourceEntityDetails, Field sourceField, Function<Class<?>, EntityDetails> entityDetailsProvider)
	{
		// if mapped by is not defined (by default it is empty string), make it
		// into null
		mappedBy = StringUtils.isBlank(mappedBy) ? null : mappedBy.trim();

		ForeignConstraintDetails details = new ForeignConstraintDetails(mappedBy, relationType, sourceField);
		details.isDeleteCascaded = (sourceField.getAnnotation(DeleteWithParent.class) != null);
		details.isSaveCascaded = isCascaded(cascadedTypes, CascadeType.PERSIST);

		Class<?> sourceFieldType = getFieldType(sourceEntityDetails.getEntityType(), sourceField, relationType.isCollectionExpected(), null);

		// fetch target entity details
		EntityDetails targetEntityDetails = entityDetailsProvider.apply(sourceFieldType);

		// ensure target represents an entity type
		if(targetEntityDetails == null)
		{
			throw new InvalidMappingException(String.format("Invalid entity relation-target type '%1s' specified for field '%2s.%3s'", sourceFieldType.getName(), sourceEntityDetails.getEntityType().getName(), sourceField.getName()));
		}

		// set target entity details on result
		details.targetEntityDetails = targetEntityDetails;
		details.ownerEntityDetails = sourceEntityDetails;
		
		// if mappedBy is used, validate target entity's target field
		if(mappedBy != null)
		{
			FieldDetails targetFieldDetails = targetEntityDetails.getFieldDetailsByField(mappedBy);

			// if mapped field is incorrect
			if(targetFieldDetails == null)
			{
				throw new InvalidMappingException(String.format("Invalid mapping field '%1s' specified for field '%2s.%3s'", mappedBy, sourceEntityDetails.getEntityType().getName(), sourceField.getName()));
			}

			Field targetField = targetFieldDetails.getField();
			Class<?> targetFieldType = getFieldType(targetEntityDetails.getEntityType(), targetField, relationType.isCollectionTargetExpected(), sourceEntityDetails.getEntityType().getName() + "." + sourceField.getName());

			// ensure target field type is matching source entity type
			if(!targetFieldType.equals(sourceEntityDetails.getEntityType()))
			{
				throw new InvalidMappingException(String.format("Entity relation target field's type '%1s.%2s' is not matching with source entity type '%3s'. Relation source field '%3s.%4s'", targetEntityDetails.getEntityType().getName(), targetField.getName(), sourceEntityDetails.getEntityType().getName(), sourceField.getName()));
			}
		}
		else
		{
			if(details.isSaveCascaded)
			{
				throw new InvalidMappingException( String.format("Save-cascade is mentioned on non-mapped relation (non-child/parent relation) '%1s.%2s'. Save cascade is supported only from parent entity to child.", 
						sourceEntityDetails.getEntityType().getName(), sourceField.getName()) );
			}
		}
		
		if(details.isSaveCascaded && relationType.isCollectionTargetExpected())
		{
			throw new InvalidMappingException( String.format("Invalid save-cascade encountered on field '%1s.%2s'. Save cascade is not supported for many-to-one and many-to-many relation.", 
					sourceEntityDetails.getEntityType().getName(), sourceField.getName()) );
		}

		// validate and get join table details, if any
		details.joinTableDetails = getJoinTableDetails(sourceEntityDetails, sourceField, mappedBy, relationType, targetEntityDetails);

		return details;
	}

	/**
	 * Fetches foreign constrain details, if one is specified
	 * 
	 * @param entityDetails
	 * @param field
	 * @param entityDetailsProvider
	 * @return
	 */
	public static ForeignConstraintDetails fetchForeignConstraint(EntityDetails entityDetails, Field field, Function<Class<?>, EntityDetails> entityDetailsProvider)
	{
		// check if field has one to one mapping
		OneToOne oneToOneMapping = field.getAnnotation(OneToOne.class);

		if(oneToOneMapping != null)
		{
			return getForeignConstraint(oneToOneMapping.mappedBy(), oneToOneMapping.cascade(), RelationType.ONE_TO_ONE, entityDetails, field, entityDetailsProvider);
		}

		// check if the field has many to one mapping
		ManyToOne manyToOneMapping = field.getAnnotation(ManyToOne.class);

		if(manyToOneMapping != null)
		{
			return getForeignConstraint(null, manyToOneMapping.cascade(), RelationType.MANY_TO_ONE, entityDetails, field, entityDetailsProvider);
		}

		// check if the field has one to many mapping
		OneToMany oneToManyMapping = field.getAnnotation(OneToMany.class);

		if(oneToManyMapping != null)
		{
			return getForeignConstraint(oneToManyMapping.mappedBy(), oneToManyMapping.cascade(), RelationType.ONE_TO_MANY, entityDetails, field, entityDetailsProvider);
		}

		// check if the field has many to many mapping
		ManyToMany manyToManyMapping = field.getAnnotation(ManyToMany.class);

		if(manyToManyMapping != null)
		{
			return getForeignConstraint(manyToManyMapping.mappedBy(), manyToManyMapping.cascade(), RelationType.MANY_TO_MANY, entityDetails, field, entityDetailsProvider);
		}

		return null;
	}

	/**
	 * Checks if the column maintaining the relation will be maintained by specified field
	 * @param field
	 * @return
	 */
	public static boolean isTableOwnedRelation(Field field)
	{
		JoinTable joinTable = field.getAnnotation(JoinTable.class);
		
		// check if field has one to one mapping
		OneToOne oneToOneMapping = field.getAnnotation(OneToOne.class);

		if(oneToOneMapping != null)
		{
			return ( !StringUtils.isBlank(oneToOneMapping.mappedBy()) && joinTable == null );
		}

		// check if the field has many to one mapping
		ManyToOne manyToOneMapping = field.getAnnotation(ManyToOne.class);

		if(manyToOneMapping != null)
		{
			return ( joinTable == null );
		}

		// check if the field has one to many mapping
		OneToMany oneToManyMapping = field.getAnnotation(OneToMany.class);

		if(oneToManyMapping != null)
		{
			return false;
		}

		// check if the field has many to many mapping
		ManyToMany manyToManyMapping = field.getAnnotation(ManyToMany.class);

		if(manyToManyMapping != null)
		{
			return false;
		}

		return true;
	}

	/**
	 * @return the {@link #mappedBy mappedBy}
	 */
	public String getMappedBy()
	{
		return mappedBy;
	}

	/**
	 * @return the {@link #isDeleteCascaded deleteCascaded}
	 */
	public boolean isDeleteCascaded()
	{
		return isDeleteCascaded;
	}
	
	/**
	 * @return the {@link #isSaveCascaded isSaveCascaded}
	 */
	public boolean isSaveCascaded()
	{
		return isSaveCascaded;
	}

	/**
	 * @return the {@link #joinTableDetails joinTableDetails}
	 */
	public JoinTableDetails getJoinTableDetails()
	{
		return joinTableDetails;
	}

	/**
	 * @return the {@link #relationType relationType}
	 */
	public RelationType getRelationType()
	{
		return relationType;
	}

	/**
	 * @return the {@link #targetEntityDetails targetEntityDetails}
	 */
	public EntityDetails getTargetEntityDetails()
	{
		return targetEntityDetails;
	}

	/**
	 * @return the {@link #ownerField ownerField}
	 */
	public Field getOwnerField()
	{
		return ownerField;
	}

	/**
	 * Indicates this is mapped relation. And actual foreign key relation is
	 * maintained by target entity and not this entity.
	 * 
	 * @return
	 */
	public boolean isMappedRelation()
	{
		return (mappedBy != null);
	}

	/**
	 * @return the {@link #ownerEntityDetails ownerEntityDetails}
	 */
	public EntityDetails getOwnerEntityDetails()
	{
		return ownerEntityDetails;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("[");

		builder.append("Mapped By: ").append(mappedBy);
		builder.append(",").append("Delete-cascaded: ").append(isDeleteCascaded);
		builder.append(",").append("Relation Type: ").append(relationType);
		builder.append(",").append("Join Table: ").append(joinTableDetails);
		builder.append(",").append("Target Entity: ").append(targetEntityDetails.getEntityType().getName());

		builder.append("]");
		return builder.toString();
	}

}
