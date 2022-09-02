/**
 * A journal entry.
 */
export interface Entry {

  /**
   * Unique identifier of this entry.
   */
  id: string,

  /**
   * Title of this entry.
   */
  title: string,

  /**
   * Text content of the entry.
   */
  content: string,

  /**
   * Author of this entry.
   */
  author: string,

  /**
   * Time when this entry was created.
   */
  created: string,

  /**
   * Optionally, time when the entry was last edited.
   */
  edited?: string

}
