package io.tolgee.ee.component.contentDelivery

import io.tolgee.constants.Message
import io.tolgee.dtos.contentDelivery.ContentStorageRequest
import io.tolgee.dtos.contentDelivery.S3ContentStorageConfigDto
import io.tolgee.exceptions.BadRequestException
import io.tolgee.model.contentDelivery.ContentStorage
import io.tolgee.model.contentDelivery.ContentStorageType
import io.tolgee.model.contentDelivery.S3ContentStorageConfig
import org.springframework.stereotype.Component
import javax.persistence.EntityManager

@Component
class S3ContentStorageConfigProcessor : ContentStorageConfigProcessor<S3ContentStorageConfig> {
  override fun getItemFromDto(dto: ContentStorageRequest): S3ContentStorageConfigDto? {
    return dto.s3ContentStorageConfig
  }

  override fun clearParentEntity(storageEntity: ContentStorage, em: EntityManager) {
    storageEntity.s3ContentStorageConfig?.let { em.remove(it) }
    storageEntity.s3ContentStorageConfig = null
  }

  override val type: ContentStorageType
    get() = ContentStorageType.S3

  override fun configDtoToEntity(
    dto: ContentStorageRequest,
    storageEntity: ContentStorage,
    em: EntityManager
  ): S3ContentStorageConfig {
    val s3dto = dto.s3ContentStorageConfig ?: throw BadRequestException(Message.S3_CONFIG_REQUIRED)
    val entity = S3ContentStorageConfig(storageEntity)
    entity.accessKey = s3dto.accessKey ?: throw BadRequestException(Message.S3_ACCESS_KEY_REQUIRED)
    entity.secretKey = s3dto.secretKey ?: throw BadRequestException(Message.S3_SECRET_KEY_REQUIRED)
    entity.bucketName = s3dto.bucketName
    entity.signingRegion = s3dto.signingRegion
    entity.endpoint = s3dto.endpoint
    storageEntity.s3ContentStorageConfig = entity
    em.persist(entity)
    return entity
  }

  override fun fillDtoSecrets(storageEntity: ContentStorage, dto: ContentStorageRequest) {
    val s3dto = dto.s3ContentStorageConfig ?: return
    val entity = storageEntity.s3ContentStorageConfig ?: return
    s3dto.accessKey = entity.accessKey
    s3dto.secretKey = entity.secretKey
  }
}