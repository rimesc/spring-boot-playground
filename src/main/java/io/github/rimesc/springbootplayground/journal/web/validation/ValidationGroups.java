package io.github.rimesc.springbootplayground.journal.web.validation;

import javax.validation.groups.Default;

/**
 * Validation groups for read, create and update contexts.
 */
public interface ValidationGroups {

  /**
   * Validations applied to request bodies received by create endpoints.
   */
  interface Create extends Default {
  }

  /**
   * Validations applied to request bodies received by update endpoints.
   */
  interface Update extends Default {
  }

  /**
   * Validations applied to response bodies returned by get/list endpoints.
   */
  interface Read extends Default {
  }

}
