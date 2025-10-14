-- WARNING: This schema is for context only and is not meant to be run.
-- Table order and constraints may not be valid for execution.

-- Ensure pgcrypto exists for gen_random_uuid()
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Create auth schema and minimal users table for FK references
CREATE SCHEMA IF NOT EXISTS auth;
CREATE TABLE auth.users (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  email text,
  created_at timestamp with time zone DEFAULT now()
);

CREATE TABLE public.Entreprise (
  id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  NomEntreprise character varying,
  SerctureActivite character varying,
  Taille bigint,
  Adresse character varying,
  CONSTRAINT Entreprise_pkey PRIMARY KEY (id)
);
CREATE TABLE public.ai_questionnaire_responses (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  questionnaire_id uuid,
  user_id uuid,
  responses jsonb NOT NULL,
  completed_at timestamp with time zone NOT NULL DEFAULT now(),
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT ai_questionnaire_responses_pkey PRIMARY KEY (id),
  CONSTRAINT ai_questionnaire_responses_user_id_fkey FOREIGN KEY (user_id) REFERENCES auth.users(id)
);
CREATE TABLE public.ai_questionnaires (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  user_id uuid,
  questionnaire_type text NOT NULL DEFAULT 'initial'::text,
  week_number integer,
  questions jsonb NOT NULL,
  generated_at timestamp with time zone NOT NULL DEFAULT now(),
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT ai_questionnaires_pkey PRIMARY KEY (id),
  CONSTRAINT ai_questionnaires_user_id_fkey FOREIGN KEY (user_id) REFERENCES auth.users(id)
);
CREATE TABLE public.ai_wellness_programs (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  user_id uuid,
  questionnaire_response_id uuid,
  program_data jsonb NOT NULL,
  wellness_scores jsonb NOT NULL,
  status text NOT NULL DEFAULT 'pending'::text,
  accepted_at timestamp with time zone,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now(),
  questionnaire_completed_at timestamp with time zone,
  CONSTRAINT ai_wellness_programs_pkey PRIMARY KEY (id),
  CONSTRAINT ai_wellness_programs_user_id_fkey FOREIGN KEY (user_id) REFERENCES auth.users(id)
);
CREATE TABLE public.booking_requests (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  employee_id uuid NOT NULL,
  workshop_id text NOT NULL,
  requested_day text NOT NULL,
  requested_time text NOT NULL,
  special_requests text,
  status text NOT NULL DEFAULT 'pending'::text,
  created_at timestamp with time zone NOT NULL DEFAULT timezone('utc'::text, now()),
  updated_at timestamp with time zone NOT NULL DEFAULT timezone('utc'::text, now()),
  expert_id uuid,
  session_completed boolean DEFAULT false,
  session_completion_date timestamp with time zone,
  is_team_booking boolean DEFAULT true,
  team_capacity integer DEFAULT 6,
  current_participants integer DEFAULT 1,
  program_session_id uuid,
  CONSTRAINT booking_requests_pkey PRIMARY KEY (id),
  CONSTRAINT booking_requests_employee_id_fkey FOREIGN KEY (employee_id) REFERENCES auth.users(id)
);
CREATE TABLE public.contacts (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  name text NOT NULL,
  email text NOT NULL,
  company text,
  phone text,
  message text NOT NULL,
  status text NOT NULL DEFAULT 'new'::text,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT contacts_pkey PRIMARY KEY (id)
);
CREATE TABLE public.email_templates (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  template_name text NOT NULL UNIQUE,
  subject text NOT NULL,
  html_content text NOT NULL,
  text_content text,
  created_at timestamp with time zone DEFAULT now(),
  updated_at timestamp with time zone DEFAULT now(),
  CONSTRAINT email_templates_pkey PRIMARY KEY (id)
);
CREATE TABLE public.email_verification_status (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  user_id uuid NOT NULL UNIQUE,
  email_verified boolean NOT NULL DEFAULT false,
  verification_sent_at timestamp with time zone DEFAULT now(),
  verified_at timestamp with time zone,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT email_verification_status_pkey PRIMARY KEY (id),
  CONSTRAINT email_verification_status_user_id_fkey FOREIGN KEY (user_id) REFERENCES auth.users(id)
);
CREATE TABLE public.employee_assessments (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  employee_id uuid NOT NULL,
  overall_rating integer NOT NULL CHECK (overall_rating >= 1 AND overall_rating <= 10),
  strengths text NOT NULL,
  areas_for_improvement text,
  recommendations text,
  goals text,
  notes text,
  assessment_date timestamp with time zone NOT NULL DEFAULT now(),
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now(),
  expert_id uuid,
  CONSTRAINT employee_assessments_pkey PRIMARY KEY (id)
);
CREATE TABLE public.employee_questionnaire_notifications (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  booking_request_id uuid NOT NULL,
  employee_id uuid NOT NULL,
  expert_id uuid NOT NULL,
  notification_sent_at timestamp with time zone NOT NULL DEFAULT now(),
  questionnaire_completed boolean NOT NULL DEFAULT false,
  questionnaire_completed_at timestamp with time zone,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT employee_questionnaire_notifications_pkey PRIMARY KEY (id)
);
CREATE TABLE public.employees (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  user_id uuid UNIQUE,
  employee_number text UNIQUE,
  first_name text NOT NULL,
  last_name text NOT NULL,
  username text NOT NULL UNIQUE,
  email text NOT NULL UNIQUE,
  department text,
  position text,
  hire_date date DEFAULT CURRENT_DATE,
  is_active boolean DEFAULT true,
  created_at timestamp with time zone NOT NULL DEFAULT timezone('utc'::text, now()),
  updated_at timestamp with time zone NOT NULL DEFAULT timezone('utc'::text, now()),
  phone_number text,
  nationality text,
  date_of_birth date,
  company_name text,
  job_title text,
  Entreprise bigint,
  latitude numeric,
  longitude numeric,
  location_address text,
  CONSTRAINT employees_pkey PRIMARY KEY (id),
  CONSTRAINT employees_user_id_fkey FOREIGN KEY (user_id) REFERENCES auth.users(id),
  CONSTRAINT employees_Entreprise_fkey FOREIGN KEY (Entreprise) REFERENCES public.Entreprise(id)
);
CREATE TABLE public.expert_availability (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  expert_id uuid,
  day_of_week text NOT NULL,
  morning boolean DEFAULT false,
  afternoon boolean DEFAULT false,
  evening boolean DEFAULT false,
  created_at timestamp with time zone NOT NULL DEFAULT timezone('utc'::text, now()),
  updated_at timestamp with time zone NOT NULL DEFAULT timezone('utc'::text, now()),
  CONSTRAINT expert_availability_pkey PRIMARY KEY (id)
);
CREATE TABLE public.expert_certifications (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  expert_id uuid NOT NULL,
  document_url text NOT NULL,
  document_name text NOT NULL,
  specialization text NOT NULL,
  status text NOT NULL DEFAULT 'pending'::text,
  verification_notes text,
  ai_analysis_result jsonb,
  verified_at timestamp with time zone,
  verified_by uuid,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now(),
  file_size bigint,
  mime_type text,
  upload_date timestamp with time zone DEFAULT now(),
  CONSTRAINT expert_certifications_pkey PRIMARY KEY (id)
);
CREATE TABLE public.expert_languages (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  expert_id uuid,
  language text NOT NULL,
  created_at timestamp with time zone NOT NULL DEFAULT timezone('utc'::text, now()),
  CONSTRAINT expert_languages_pkey PRIMARY KEY (id)
);
CREATE TABLE public.expert_matches (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  booking_request_id uuid NOT NULL,
  expert_id uuid NOT NULL,
  status text NOT NULL DEFAULT 'pending'::text,
  match_score integer NOT NULL,
  created_at timestamp with time zone NOT NULL DEFAULT timezone('utc'::text, now()),
  updated_at timestamp with time zone NOT NULL DEFAULT timezone('utc'::text, now()),
  CONSTRAINT expert_matches_pkey PRIMARY KEY (id)
);
CREATE TABLE public.experts (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  user_id uuid UNIQUE,
  first_name text NOT NULL,
  last_name text NOT NULL,
  specialization text NOT NULL,
  years_of_experience integer NOT NULL,
  rate_per_hour numeric NOT NULL,
  biography text NOT NULL,
  created_at timestamp with time zone NOT NULL DEFAULT timezone('utc'::text, now()),
  updated_at timestamp with time zone NOT NULL DEFAULT timezone('utc'::text, now()),
  certifications text,
  phone_number text,
  email text,
  is_certified boolean DEFAULT false,
  certification_status text DEFAULT 'pending'::text,
  certification_document_url text,
  certification_verified_at timestamp with time zone,
  certification_verification_notes text,
  latitude numeric,
  longitude numeric,
  location_address text,
  CONSTRAINT experts_pkey PRIMARY KEY (id),
  CONSTRAINT experts_user_id_fkey FOREIGN KEY (user_id) REFERENCES auth.users(id)
);
CREATE TABLE public.hr_personnel (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  user_id uuid,
  company_name text NOT NULL,
  job_title text NOT NULL,
  employee_count text,
  industry text,
  created_at timestamp with time zone NOT NULL DEFAULT timezone('utc'::text, now()),
  updated_at timestamp with time zone NOT NULL DEFAULT timezone('utc'::text, now()),
  phone_number text,
  CONSTRAINT hr_personnel_pkey PRIMARY KEY (id),
  CONSTRAINT hr_personnel_user_id_fkey FOREIGN KEY (user_id) REFERENCES auth.users(id)
);
CREATE TABLE public.kpi_data (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  hr_user_id uuid NOT NULL,
  kpi_type text NOT NULL CHECK (kpi_type = ANY (ARRAY['absenteeism'::text, 'healthcare_costs'::text, 'retention'::text, 'productivity'::text])),
  department text,
  metric_value numeric NOT NULL,
  metric_date date NOT NULL,
  period_type text NOT NULL CHECK (period_type = ANY (ARRAY['daily'::text, 'weekly'::text, 'monthly'::text, 'quarterly'::text])),
  additional_data jsonb,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT kpi_data_pkey PRIMARY KEY (id)
);
CREATE TABLE public.message_sessions (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  hr_user_id uuid NOT NULL,
  employee_user_id uuid NOT NULL,
  last_message_at timestamp with time zone NOT NULL DEFAULT now(),
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT message_sessions_pkey PRIMARY KEY (id)
);
CREATE TABLE public.messages (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  sender_id uuid NOT NULL,
  recipient_id uuid NOT NULL,
  content text NOT NULL,
  is_read boolean NOT NULL DEFAULT false,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT messages_pkey PRIMARY KEY (id)
);
CREATE TABLE public.newsletter_subscribers (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  email text NOT NULL UNIQUE,
  subscribed_at timestamp with time zone NOT NULL DEFAULT now(),
  is_active boolean NOT NULL DEFAULT true,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT newsletter_subscribers_pkey PRIMARY KEY (id)
);
CREATE TABLE public.profiles (
  id uuid NOT NULL,
  username text UNIQUE,
  first_name text,
  last_name text,
  date_of_birth date,
  height numeric,
  weight numeric,
  position text,
  department text,
  avatar_url text,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now(),
  phone_number text,
  CONSTRAINT profiles_pkey PRIMARY KEY (id),
  CONSTRAINT profiles_id_fkey FOREIGN KEY (id) REFERENCES auth.users(id)
);
CREATE TABLE public.program_sessions (
  user_id uuid,
  workshop_id character varying,
  workshop_name character varying,
  session_number integer,
  scheduled_date timestamp without time zone,
  status character varying DEFAULT 'pending'::character varying,
  created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
  program_id uuid,
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  booking_request_id uuid,
  CONSTRAINT program_sessions_pkey PRIMARY KEY (id),
  CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES auth.users(id)
);
CREATE TABLE public.questionnaire_responses (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  user_id uuid NOT NULL,
  question_id integer NOT NULL,
  response text NOT NULL,
  category text NOT NULL,
  created_at timestamp with time zone NOT NULL DEFAULT timezone('utc'::text, now()),
  updated_at timestamp with time zone NOT NULL DEFAULT timezone('utc'::text, now()),
  CONSTRAINT questionnaire_responses_pkey PRIMARY KEY (id)
);
CREATE TABLE public.session_feedback (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  booking_request_id uuid NOT NULL,
  feedback_type text NOT NULL CHECK (feedback_type = ANY (ARRAY['employee_feedback'::text, 'expert_feedback'::text])),
  user_id uuid NOT NULL,
  rating integer CHECK (rating >= 1 AND rating <= 5),
  session_quality_rating integer CHECK (session_quality_rating >= 1 AND session_quality_rating <= 5),
  communication_rating integer CHECK (communication_rating >= 1 AND communication_rating <= 5),
  professionalism_rating integer CHECK (professionalism_rating >= 1 AND professionalism_rating <= 5),
  feedback_text text,
  would_recommend boolean,
  session_helpful boolean,
  suggestions_for_improvement text,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT session_feedback_pkey PRIMARY KEY (id)
);
CREATE TABLE public.session_notifications (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  booking_request_id uuid NOT NULL,
  recipient_id uuid NOT NULL,
  notification_type text NOT NULL CHECK (notification_type = ANY (ARRAY['reminder'::text, 'confirmation'::text, 'cancellation'::text])),
  title text NOT NULL,
  message text NOT NULL,
  is_read boolean NOT NULL DEFAULT false,
  sent_at timestamp with time zone NOT NULL DEFAULT now(),
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT session_notifications_pkey PRIMARY KEY (id)
);
CREATE TABLE public.survey_questions (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  survey_id uuid NOT NULL,
  question_text text NOT NULL,
  question_order integer NOT NULL,
  question_type text NOT NULL DEFAULT 'text'::text CHECK (question_type = ANY (ARRAY['text'::text, 'multiple_choice'::text, 'rating'::text, 'yes_no'::text])),
  options jsonb,
  is_required boolean NOT NULL DEFAULT true,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT survey_questions_pkey PRIMARY KEY (id)
);
CREATE TABLE public.survey_responses (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  survey_id uuid NOT NULL,
  question_id uuid NOT NULL,
  employee_user_id uuid NOT NULL,
  response_text text,
  response_value integer,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT survey_responses_pkey PRIMARY KEY (id),
  CONSTRAINT survey_responses_employee_user_id_fkey FOREIGN KEY (employee_user_id) REFERENCES auth.users(id)
);
CREATE TABLE public.surveys (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  hr_user_id uuid NOT NULL,
  employee_user_id uuid NOT NULL,
  title text NOT NULL DEFAULT 'Employee Survey'::text,
  description text,
  status text NOT NULL DEFAULT 'pending'::text CHECK (status = ANY (ARRAY['pending'::text, 'completed'::text, 'expired'::text])),
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now(),
  completed_at timestamp with time zone,
  expires_at timestamp with time zone,
  CONSTRAINT surveys_pkey PRIMARY KEY (id),
  CONSTRAINT surveys_hr_user_id_fkey FOREIGN KEY (hr_user_id) REFERENCES auth.users(id),
  CONSTRAINT surveys_employee_user_id_fkey FOREIGN KEY (employee_user_id) REFERENCES auth.users(id)
);
CREATE TABLE public.team_booking_participants (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  booking_request_id uuid NOT NULL,
  participant_id uuid NOT NULL,
  joined_at timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT team_booking_participants_pkey PRIMARY KEY (id)
);
CREATE TABLE public.team_invitations (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  team_id uuid NOT NULL,
  invited_by uuid NOT NULL,
  invited_user_id uuid NOT NULL,
  status text NOT NULL DEFAULT 'pending'::text CHECK (status = ANY (ARRAY['pending'::text, 'accepted'::text, 'declined'::text])),
  message text,
  invited_at timestamp with time zone NOT NULL DEFAULT now(),
  responded_at timestamp with time zone,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT team_invitations_pkey PRIMARY KEY (id),
  CONSTRAINT team_invitations_invited_by_fkey FOREIGN KEY (invited_by) REFERENCES auth.users(id),
  CONSTRAINT team_invitations_invited_user_id_fkey FOREIGN KEY (invited_user_id) REFERENCES auth.users(id)
);
CREATE TABLE public.team_join_requests (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  team_id uuid NOT NULL,
  user_id uuid NOT NULL,
  status text NOT NULL DEFAULT 'pending'::text CHECK (status = ANY (ARRAY['pending'::text, 'approved'::text, 'rejected'::text])),
  message text,
  requested_at timestamp with time zone NOT NULL DEFAULT now(),
  processed_at timestamp with time zone,
  processed_by uuid,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT team_join_requests_pkey PRIMARY KEY (id),
  CONSTRAINT team_join_requests_user_id_fkey FOREIGN KEY (user_id) REFERENCES auth.users(id),
  CONSTRAINT team_join_requests_processed_by_fkey FOREIGN KEY (processed_by) REFERENCES auth.users(id)
);
CREATE TABLE public.team_members (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  team_id uuid NOT NULL,
  user_id uuid NOT NULL,
  role text NOT NULL DEFAULT 'member'::text,
  joined_at timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT team_members_pkey PRIMARY KEY (id),
  CONSTRAINT fk_team_members_user_id FOREIGN KEY (user_id) REFERENCES auth.users(id)
);
CREATE TABLE public.team_session_bookings (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  team_id uuid NOT NULL,
  booking_request_id uuid NOT NULL,
  assigned_members uuid[] NOT NULL DEFAULT '{}'::uuid[],
  created_by uuid NOT NULL,
  session_date date NOT NULL,
  session_time text NOT NULL,
  workshop_type text NOT NULL,
  notes text,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT team_session_bookings_pkey PRIMARY KEY (id),
  CONSTRAINT team_session_bookings_created_by_fkey FOREIGN KEY (created_by) REFERENCES auth.users(id)
);
CREATE TABLE public.teams (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  name text NOT NULL,
  description text,
  leader_id uuid NOT NULL,
  max_members integer NOT NULL DEFAULT 6,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now(),
  team_type text DEFAULT 'open'::text CHECK (team_type = ANY (ARRAY['open'::text, 'closed'::text])),
  status text DEFAULT 'active'::text CHECK (status = ANY (ARRAY['active'::text, 'inactive'::text])),
  CONSTRAINT teams_pkey PRIMARY KEY (id)
);
CREATE TABLE public.trusted_partners (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  company_name text NOT NULL,
  logo_url text NOT NULL,
  alt_text text NOT NULL,
  display_order integer NOT NULL DEFAULT 0,
  is_active boolean NOT NULL DEFAULT true,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT trusted_partners_pkey PRIMARY KEY (id)
);
CREATE TABLE public.user_backgrounds (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  user_id uuid NOT NULL UNIQUE,
  background_type character varying NOT NULL CHECK (background_type::text = ANY (ARRAY['preset'::character varying, 'custom'::character varying, 'color'::character varying]::text[])),
  background_value text NOT NULL,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT user_backgrounds_pkey PRIMARY KEY (id)
);
CREATE TABLE public.weekly_wellness_surveys (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  user_id uuid,
  week_number integer NOT NULL,
  year integer NOT NULL DEFAULT EXTRACT(year FROM now()),
  questionnaire_id uuid,
  response_id uuid,
  wellness_index numeric,
  wellness_scores jsonb,
  status text NOT NULL DEFAULT 'pending'::text,
  due_date timestamp with time zone NOT NULL,
  completed_at timestamp with time zone,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT weekly_wellness_surveys_pkey PRIMARY KEY (id),
  CONSTRAINT weekly_wellness_surveys_user_id_fkey FOREIGN KEY (user_id) REFERENCES auth.users(id),
  CONSTRAINT weekly_wellness_surveys_questionnaire_id_fkey FOREIGN KEY (questionnaire_id) REFERENCES public.ai_questionnaires(id),
  CONSTRAINT weekly_wellness_surveys_response_id_fkey FOREIGN KEY (response_id) REFERENCES public.ai_questionnaire_responses(id)
);
CREATE TABLE public.wellness_progress_tracking (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  user_id uuid,
  tracking_date date NOT NULL DEFAULT CURRENT_DATE,
  wellness_index numeric NOT NULL,
  category_scores jsonb NOT NULL,
  program_adherence numeric,
  notes text,
  created_at timestamp with time zone NOT NULL DEFAULT now(),
  updated_at timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT wellness_progress_tracking_pkey PRIMARY KEY (id),
  CONSTRAINT wellness_progress_tracking_user_id_fkey FOREIGN KEY (user_id) REFERENCES auth.users(id)
);
CREATE TABLE public.workshop_benefits (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  workshop_id uuid NOT NULL,
  benefit text NOT NULL,
  created_at timestamp with time zone NOT NULL DEFAULT timezone('utc'::text, now()),
  CONSTRAINT workshop_benefits_pkey PRIMARY KEY (id)
);
CREATE TABLE public.workshop_schedules (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  workshop_id uuid NOT NULL,
  day text NOT NULL,
  time text NOT NULL,
  created_at timestamp with time zone NOT NULL DEFAULT timezone('utc'::text, now()),
  updated_at timestamp with time zone NOT NULL DEFAULT timezone('utc'::text, now()),
  CONSTRAINT workshop_schedules_pkey PRIMARY KEY (id)
);
CREATE TABLE public.workshops (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  name text NOT NULL,
  description text,
  instructor text NOT NULL,
  duration integer NOT NULL,
  capacity integer NOT NULL,
  location text NOT NULL,
  created_at timestamp with time zone NOT NULL DEFAULT timezone('utc'::text, now()),
  updated_at timestamp with time zone NOT NULL DEFAULT timezone('utc'::text, now()),
  default_capacity integer DEFAULT 6,
  CONSTRAINT workshops_pkey PRIMARY KEY (id)
);

-- Add all foreign keys after all tables are created
ALTER TABLE public.ai_questionnaire_responses
  ADD CONSTRAINT ai_questionnaire_responses_questionnaire_id_fkey FOREIGN KEY (questionnaire_id) REFERENCES public.ai_questionnaires(id);

-- Booking requests foreign keys
ALTER TABLE public.booking_requests
  ADD CONSTRAINT booking_requests_expert_id_fkey FOREIGN KEY (expert_id) REFERENCES public.experts(id),
  ADD CONSTRAINT fk_program_session FOREIGN KEY (program_session_id) REFERENCES public.program_sessions(id);

-- Employee assessments foreign keys
ALTER TABLE public.employee_assessments
  ADD CONSTRAINT employee_assessments_employee_id_fkey FOREIGN KEY (employee_id) REFERENCES public.employees(user_id);

-- Employee questionnaire notifications foreign keys
ALTER TABLE public.employee_questionnaire_notifications
  ADD CONSTRAINT fk_employee_id FOREIGN KEY (employee_id) REFERENCES public.employees(user_id),
  ADD CONSTRAINT fk_booking_request FOREIGN KEY (booking_request_id) REFERENCES public.booking_requests(id),
  ADD CONSTRAINT fk_expert_id FOREIGN KEY (expert_id) REFERENCES public.experts(id);

-- Expert tables foreign keys
ALTER TABLE public.expert_availability
  ADD CONSTRAINT expert_availability_expert_id_fkey FOREIGN KEY (expert_id) REFERENCES public.experts(id);

ALTER TABLE public.expert_certifications
  ADD CONSTRAINT expert_certifications_expert_id_fkey FOREIGN KEY (expert_id) REFERENCES public.experts(id);

ALTER TABLE public.expert_languages
  ADD CONSTRAINT expert_languages_expert_id_fkey FOREIGN KEY (expert_id) REFERENCES public.experts(id);

ALTER TABLE public.expert_matches
  ADD CONSTRAINT expert_matches_booking_request_id_fkey FOREIGN KEY (booking_request_id) REFERENCES public.booking_requests(id),
  ADD CONSTRAINT expert_matches_expert_id_fkey FOREIGN KEY (expert_id) REFERENCES public.experts(id);

-- Messages foreign key
ALTER TABLE public.messages
  ADD CONSTRAINT fk_sender FOREIGN KEY (sender_id) REFERENCES public.profiles(id);

-- Program sessions foreign keys
ALTER TABLE public.program_sessions
  ADD CONSTRAINT fk_program FOREIGN KEY (program_id) REFERENCES public.ai_wellness_programs(id),
  ADD CONSTRAINT fk_booking_request FOREIGN KEY (booking_request_id) REFERENCES public.booking_requests(id);

-- Session feedback foreign key
ALTER TABLE public.session_feedback
  ADD CONSTRAINT session_feedback_booking_request_id_fkey FOREIGN KEY (booking_request_id) REFERENCES public.booking_requests(id);

-- Survey tables foreign keys
ALTER TABLE public.survey_questions
  ADD CONSTRAINT survey_questions_survey_id_fkey FOREIGN KEY (survey_id) REFERENCES public.surveys(id);

ALTER TABLE public.survey_responses
  ADD CONSTRAINT survey_responses_survey_id_fkey FOREIGN KEY (survey_id) REFERENCES public.surveys(id),
  ADD CONSTRAINT survey_responses_question_id_fkey FOREIGN KEY (question_id) REFERENCES public.survey_questions(id);

-- Team tables foreign keys
ALTER TABLE public.team_booking_participants
  ADD CONSTRAINT team_booking_participants_booking_request_id_fkey FOREIGN KEY (booking_request_id) REFERENCES public.booking_requests(id);

ALTER TABLE public.team_invitations
  ADD CONSTRAINT team_invitations_team_id_fkey FOREIGN KEY (team_id) REFERENCES public.teams(id);

ALTER TABLE public.team_join_requests
  ADD CONSTRAINT team_join_requests_team_id_fkey FOREIGN KEY (team_id) REFERENCES public.teams(id);

ALTER TABLE public.team_members
  ADD CONSTRAINT fk_team_members_team_id FOREIGN KEY (team_id) REFERENCES public.teams(id),
  ADD CONSTRAINT team_members_team_id_fkey FOREIGN KEY (team_id) REFERENCES public.teams(id),
  ADD CONSTRAINT teams_team_members_team_id_fkey FOREIGN KEY (team_id) REFERENCES public.teams(id),
  ADD CONSTRAINT team_members_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.employees(user_id);

ALTER TABLE public.team_session_bookings
  ADD CONSTRAINT team_session_bookings_team_id_fkey FOREIGN KEY (team_id) REFERENCES public.teams(id),
  ADD CONSTRAINT team_session_bookings_booking_request_id_fkey FOREIGN KEY (booking_request_id) REFERENCES public.booking_requests(id);

-- Workshop tables foreign keys
ALTER TABLE public.workshop_benefits
  ADD CONSTRAINT workshop_benefits_workshop_id_fkey FOREIGN KEY (workshop_id) REFERENCES public.workshops(id);

ALTER TABLE public.workshop_schedules
  ADD CONSTRAINT workshop_schedules_workshop_id_fkey FOREIGN KEY (workshop_id) REFERENCES public.workshops(id);