package io.lackstudio.omnifeed.unsplash.data.remote.model.dto

interface UserProfileDto : UserDto {
    val photos: List<PreviewPhotoDto>
    val badge: BadgeDto?
    val tags: TagsDto?
    val allowMessages: Boolean?
    val numericId: Long?
    val downloads: Long?
    val meta: MetaDto?
}
