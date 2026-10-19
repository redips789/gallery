/**
 * Shared id generator for all entities in this package. The name and allocation size are pinned so ids
 * keep coming from HIBERNATE_SEQUENCE one at a time, as they did before the Hibernate upgrade.
 */
@SequenceGenerator(name = "hibernateSequence", sequenceName = "HIBERNATE_SEQUENCE", allocationSize = 1)
package lt.insoft.training.model;

import jakarta.persistence.SequenceGenerator;
