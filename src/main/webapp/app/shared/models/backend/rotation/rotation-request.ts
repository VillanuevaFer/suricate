import { RotationProjectRequest } from '../rotation-project/rotation-project-request';

/**
 * The rotation request
 */
export interface RotationRequest {
  /**
   * The rotation name
   */
  name: string;

  /**
   * Does the progress bar should be displayed
   */
  progressBar: boolean;
}