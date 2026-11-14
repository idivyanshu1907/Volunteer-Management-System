CREATE DATABASE IF NOT EXISTS volunteer_management_vms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE volunteer_management_vms;
CREATE TABLE IF NOT EXISTS departments (
  department_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  department_name VARCHAR(100) NOT NULL UNIQUE
);
CREATE TABLE IF NOT EXISTS users (
  user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  full_name VARCHAR(100) NOT NULL,
  registration_number VARCHAR(30) UNIQUE,
  email VARCHAR(150) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  role ENUM('STUDENT','HOST','HOD','PROCTOR') NOT NULL,
  department_id BIGINT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_users_department FOREIGN KEY (department_id) REFERENCES departments(department_id)
);
CREATE TABLE IF NOT EXISTS events (
  event_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(150) NOT NULL,
  description TEXT,
  event_date DATE NOT NULL,
  event_time TIME NOT NULL,
  venue VARCHAR(180) NOT NULL,
  volunteers_required INT NOT NULL,
  registration_deadline DATE NOT NULL,
  reward VARCHAR(150),
  host_id BIGINT NOT NULL,
  hod_id BIGINT NOT NULL,
  status ENUM('PENDING','APPROVED','REJECTED','CHANGES_REQUESTED','CANCELLED') NOT NULL DEFAULT 'PENDING',
  hod_feedback VARCHAR(500),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_events_host FOREIGN KEY (host_id) REFERENCES users(user_id),
  CONSTRAINT fk_events_hod FOREIGN KEY (hod_id) REFERENCES users(user_id),
  CONSTRAINT chk_event_volunteers CHECK (volunteers_required > 0)
);
CREATE TABLE IF NOT EXISTS registrations (
  registration_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  event_id BIGINT NOT NULL,
  student_id BIGINT NOT NULL,
  status ENUM('PENDING','ACCEPTED','REJECTED') NOT NULL DEFAULT 'PENDING',
  applied_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  reviewed_at TIMESTAMP NULL,
  UNIQUE KEY uq_event_student (event_id, student_id),
  CONSTRAINT fk_reg_event FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
  CONSTRAINT fk_reg_student FOREIGN KEY (student_id) REFERENCES users(user_id)
);
CREATE TABLE IF NOT EXISTS attendance (
  attendance_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  event_id BIGINT NOT NULL,
  student_id BIGINT NOT NULL,
  status ENUM('PRESENT','ABSENT') NOT NULL,
  marked_by BIGINT NOT NULL,
  marked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uq_attendance_event_student (event_id, student_id),
  CONSTRAINT fk_att_event FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
  CONSTRAINT fk_att_student FOREIGN KEY (student_id) REFERENCES users(user_id),
  CONSTRAINT fk_att_marker FOREIGN KEY (marked_by) REFERENCES users(user_id)
);
CREATE TABLE IF NOT EXISTS audit_logs (
  audit_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  actor_id BIGINT NULL,
  action VARCHAR(100) NOT NULL,
  entity_type VARCHAR(50) NOT NULL,
  entity_id BIGINT NULL,
  details VARCHAR(1000),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_audit_actor FOREIGN KEY (actor_id) REFERENCES users(user_id) ON DELETE SET NULL
);
