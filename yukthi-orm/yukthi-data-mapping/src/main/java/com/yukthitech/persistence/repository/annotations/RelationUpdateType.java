package com.yukthitech.persistence.repository.annotations;

/**
 * Specifies the type of update operation to be performed on a relation field during update operations.
 */
public enum RelationUpdateType 
{
    /**
     * No relation update operation will be performed. (Default)
     */
    NONE,

    /**
     * Synchronize the relation: add new, ignore existing, sever missing relations.
     */
    SYNC_RELATION,

    /**
     * Cascade update: update sub-entities, remove those not present in the input.
     */
    CASCADE,
} 