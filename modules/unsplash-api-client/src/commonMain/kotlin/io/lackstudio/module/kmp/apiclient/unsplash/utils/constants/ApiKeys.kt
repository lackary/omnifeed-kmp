package io.lackstudio.module.kmp.apiclient.unsplash.utils.constants

internal object ApiKeys {
    object Params {
        const val CLIENT_ID = "client_id"
        const val PAGE = "page"
        const val PER_PAGE = "per_page"
        const val ORDER_BY = "order_by"
        const val STATS = "stats"
        const val RESOLUTION = "resolution"
        const val QUANTITY = "quantity"
        const val ORIENTATION = "orientation"
        const val QUERY = "query"
        const val COLLECTIONS = "collections"
        const val TOPICS = "topics"
        const val USERNAME = "username"
        const val CONTENT_FILTER = "content_filter"
        const val COUNT = "count"
        const val COLOR = "color"
    }

    // Common
    object Common {
        const val USER_ID = "user_id"
        const val CREATED_AT = "created_at"
        const val UPDATED_AT = "updated_at"
        const val PUBLISHED_AT = "published_at"
        const val STARTS_AT = "starts_at"
        const val ENDS_AT = "ends_at"
        const val PROFILE_IMAGE = "profile_image"
        const val FOR_HIRE = "for_hire"
        const val MEDIA_TYPES = "media_types"
        const val COVER_PHOTO = "cover_photo"
        const val PREVIEW_PHOTOS = "preview_photos"
    }

    object Error {
        const val ERROR_DESCRIPTION = "error_description"
    }

    object Token {
        const val ACCESS_TOKEN = "access_token"
        const val TOKEN_TYPE = "token_type"
        const val REFRESH_TOKEN = "refresh_token"
        const val USER_ID = "user_id"
    }

    // User
    object User {
        const val ACCEPTED_TOS = "accepted_tos"
        const val ALLOW_MESSAGES = "allow_messages"
        const val FIRST_NAME = "first_name"
        const val LAST_NAME = "last_name"
        const val NUMERIC_ID = "numeric_id"
    }

    object Photo {
        const val BLUR_HASH = "blur_hash"
        const val ASSET_TYPE = "asset_type"
        const val ALTERNATIVE_SLUGS = "alternative_slugs"
        const val PROMOTED_AT = "promoted_at"
        const val ALT_DESCRIPTION = "alt_description"
        const val LIKED_BY_USER = "liked_by_user"
        const val CURRENT_USER_COLLECTIONS = "current_user_collections"
        const val TOPIC_SUBMISSIONS = "topic_submissions"
        const val SHOW_ON_PROFILE = "show_on_profile"
        const val DOWNLOAD_LOCATION = "download_location"
        const val PUBLIC_DOMAIN = "public_domain"
        const val RELATED_COLLECTIONS = "related_collections"
    }

    object Collection {
        const val LAST_COLLECTED_AT = "last_collected_at"
        const val SHARE_KEY = "share_key"
    }

    object Search {
        const val TOTAL_PAGES = "total_pages"
    }

    object Topic {
        const val TOP_CONTRIBUTORS = "top_contributors"
        const val APPROVED_ON = "approved_on"
    }

    object Social {
        const val TWITTER_USERNAME = "twitter_username"
        const val PORTFOLIO_URL = "portfolio_url"
        const val INSTAGRAM_USERNAME = "instagram_username"
        const val PAYPAL_EMAIL = "paypal_email"
    }

    object Statistics {
        const val TOTAL_COLLECTIONS = "total_collections"
        const val TOTAL_LIKES = "total_likes"
        const val TOTAL_PHOTOS = "total_photos"
        const val TOTAL_PROMOTED_PHOTOS = "total_promoted_photos"
        const val TOTAL_ILLUSTRATIONS = "total_illustrations"
        const val TOTAL_PROMOTED_ILLUSTRATIONS = "total_promoted_illustrations"
    }

    object Urls {
        const val SMALL_S3 = "small_s3"
    }

    object Sponsorship {
        const val IMPRESSION_URLS = "impression_urls"
        const val TAGLINE_URL = "tagline_url"
    }

    object Exif {
        const val EXPOSURE_TIME = "exposure_time"
        const val FOCAL_LENGTH = "focal_length"
    }
}
