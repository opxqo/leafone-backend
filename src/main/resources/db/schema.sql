-- LeafOne Forum Database Schema
-- MySQL 8.0+

CREATE DATABASE IF NOT EXISTS leafone DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE leafone;

-- 1. users
CREATE TABLE users (
    id BIGINT PRIMARY KEY,
    openid VARCHAR(64) NOT NULL DEFAULT '',
    unionid VARCHAR(64) DEFAULT NULL,
    phone VARCHAR(20) DEFAULT NULL,
    student_no VARCHAR(32) DEFAULT NULL,
    dorm VARCHAR(64) DEFAULT NULL,
    password_hash VARCHAR(255) DEFAULT NULL,
    nickname VARCHAR(64) NOT NULL DEFAULT '',
    avatar_url VARCHAR(512) NOT NULL DEFAULT '',
    gender TINYINT NOT NULL DEFAULT 0,
    role VARCHAR(32) NOT NULL DEFAULT 'USER',
    status TINYINT NOT NULL DEFAULT 1,
    last_login_at DATETIME DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME DEFAULT NULL,
    UNIQUE INDEX uk_users_openid (openid),
    UNIQUE INDEX uk_users_student_no (student_no),
    INDEX idx_users_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. student_profiles
CREATE TABLE student_profiles (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    real_name VARCHAR(64) NOT NULL DEFAULT '',
    student_no VARCHAR(32) NOT NULL DEFAULT '',
    college VARCHAR(128) NOT NULL DEFAULT '',
    major VARCHAR(128) NOT NULL DEFAULT '',
    grade VARCHAR(32) NOT NULL DEFAULT '',
    identity_label VARCHAR(32) NOT NULL DEFAULT '',
    room_name VARCHAR(64) DEFAULT NULL,
    dorm_password VARCHAR(255) DEFAULT NULL,
    verified TINYINT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_student_profiles_user (user_id),
    INDEX idx_student_profiles_no (student_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. modules
CREATE TABLE modules (
    id BIGINT PRIMARY KEY,
    code VARCHAR(64) NOT NULL,
    name VARCHAR(64) NOT NULL DEFAULT '',
    description VARCHAR(255) NOT NULL DEFAULT '',
    icon_key VARCHAR(64) NOT NULL DEFAULT '',
    sort_order INT NOT NULL DEFAULT 0,
    post_count_today INT NOT NULL DEFAULT 0,
    enabled TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_modules_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. posts
CREATE TABLE posts (
    id BIGINT PRIMARY KEY,
    author_id BIGINT NOT NULL,
    module_id BIGINT NOT NULL,
    title VARCHAR(120) NOT NULL DEFAULT '',
    summary VARCHAR(255) NOT NULL DEFAULT '',
    content TEXT,
    cover_url VARCHAR(512) DEFAULT NULL,
    cover_type VARCHAR(32) DEFAULT NULL,
    price DECIMAL(10,2) DEFAULT NULL,
    location_name VARCHAR(128) DEFAULT NULL,
    status TINYINT NOT NULL DEFAULT 0,
    is_pinned TINYINT NOT NULL DEFAULT 0,
    is_featured TINYINT NOT NULL DEFAULT 0,
    view_count INT NOT NULL DEFAULT 0,
    share_count INT NOT NULL DEFAULT 0,
    comment_count INT NOT NULL DEFAULT 0,
    like_count INT NOT NULL DEFAULT 0,
    favorite_count INT NOT NULL DEFAULT 0,
    published_at DATETIME DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME DEFAULT NULL,
    INDEX idx_posts_module_status_time (module_id, status, published_at),
    INDEX idx_posts_author_time (author_id, published_at),
    INDEX idx_posts_featured_time (is_featured, published_at),
    INDEX idx_posts_pinned_time (is_pinned, published_at),
    INDEX idx_posts_status_published (status, published_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. post_attachments
CREATE TABLE post_attachments (
    id BIGINT PRIMARY KEY,
    post_id BIGINT NOT NULL,
    display_name VARCHAR(255) NOT NULL DEFAULT '',
    file_url VARCHAR(512) NOT NULL DEFAULT '',
    file_size VARCHAR(32) NOT NULL DEFAULT '',
    file_type VARCHAR(32) NOT NULL DEFAULT '',
    sort_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_post_attachments_post (post_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. files
CREATE TABLE files (
    id BIGINT PRIMARY KEY,
    owner_id BIGINT NOT NULL,
    bucket VARCHAR(128) NOT NULL DEFAULT '',
    object_key VARCHAR(512) NOT NULL DEFAULT '',
    url VARCHAR(512) NOT NULL DEFAULT '',
    mime_type VARCHAR(128) NOT NULL DEFAULT '',
    file_type VARCHAR(32) NOT NULL DEFAULT '',
    size_bytes BIGINT NOT NULL DEFAULT 0,
    sha256 VARCHAR(128) DEFAULT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_files_owner (owner_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. post_comments
CREATE TABLE post_comments (
    id BIGINT PRIMARY KEY,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    parent_id BIGINT DEFAULT NULL,
    reply_to_user_id BIGINT DEFAULT NULL,
    content VARCHAR(1000) NOT NULL DEFAULT '',
    like_count INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME DEFAULT NULL,
    INDEX idx_comments_post_time (post_id, created_at),
    INDEX idx_comments_parent_time (parent_id, created_at),
    INDEX idx_comments_user_time (user_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. reactions
CREATE TABLE reactions (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    target_type VARCHAR(32) NOT NULL,
    target_id BIGINT NOT NULL,
    reaction_type VARCHAR(32) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at DATETIME DEFAULT NULL,
    UNIQUE INDEX uk_reaction (user_id, target_type, target_id, reaction_type),
    INDEX idx_reaction_target (target_type, target_id, reaction_type, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. user_follows
CREATE TABLE user_follows (
    id BIGINT PRIMARY KEY,
    follower_id BIGINT NOT NULL,
    following_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at DATETIME DEFAULT NULL,
    UNIQUE INDEX uk_follow (follower_id, following_id),
    INDEX idx_follows_following (following_id, follower_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. topics
CREATE TABLE topics (
    id BIGINT PRIMARY KEY,
    name VARCHAR(64) NOT NULL DEFAULT '',
    description VARCHAR(255) NOT NULL DEFAULT '',
    heat_score INT NOT NULL DEFAULT 0,
    post_count INT NOT NULL DEFAULT 0,
    enabled TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_topics_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 11. messages
CREATE TABLE messages (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    sender_id BIGINT DEFAULT NULL,
    type VARCHAR(32) NOT NULL,
    title VARCHAR(64) NOT NULL DEFAULT '',
    content VARCHAR(255) NOT NULL DEFAULT '',
    target_type VARCHAR(32) DEFAULT NULL,
    target_id BIGINT DEFAULT NULL,
    read_at DATETIME DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at DATETIME DEFAULT NULL,
    INDEX idx_messages_user_read_time (user_id, read_at, created_at),
    INDEX idx_messages_user_type_time (user_id, type, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 12. home_headlines
CREATE TABLE home_headlines (
    id BIGINT PRIMARY KEY,
    title VARCHAR(128) NOT NULL DEFAULT '',
    summary VARCHAR(255) NOT NULL DEFAULT '',
    cover_url VARCHAR(512) NOT NULL DEFAULT '',
    target_type VARCHAR(32) NOT NULL,
    target_id BIGINT DEFAULT NULL,
    external_url VARCHAR(512) DEFAULT NULL,
    is_pinned TINYINT NOT NULL DEFAULT 0,
    view_count INT NOT NULL DEFAULT 0,
    published_at DATETIME DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_headlines_pinned_published (is_pinned, published_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 13. dorm_power_records
CREATE TABLE dorm_power_records (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    room_name VARCHAR(64) NOT NULL DEFAULT '',
    record_time DATETIME NOT NULL,
    power_kwh DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    record_type VARCHAR(16) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_dorm_power_user_time (user_id, record_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 14. feedbacks
CREATE TABLE feedbacks (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    content VARCHAR(1000) NOT NULL DEFAULT '',
    contact VARCHAR(64) DEFAULT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_feedbacks_user (user_id),
    INDEX idx_feedbacks_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
