-- SQL
-- Minimal dummy data for integration tests
-- Inserts: genres, users (+favorite genres), vhs, rentals, reviews

-- Genres (4) - unique(name) exists
INSERT INTO genres (id, name) VALUES
                                  ('71111111-aaaa-4a1a-8000-000000000001', 'Action'),
                                  ('71111111-aaaa-4a1a-8000-000000000002', 'Drama'),
                                  ('71111111-aaaa-4a1a-8000-000000000003', 'Comedy'),
                                  ('71111111-aaaa-4a1a-8000-000000000004', 'Science Fiction')
ON CONFLICT (name) DO NOTHING;

-- Users (3) - unique(email) exists
INSERT INTO users (id, name, email, password) VALUES
                                                  ('72222222-bbbb-4b2b-9000-000000000001', 'Test Alice', 'test.alice@example.com', '$2a$12$JozoqAf/8G70zk/crcoJJ./jKpPU9Zo0NP52CRgEAB0bgIUG8s2gi'),
                                                  ('72222222-bbbb-4b2b-9000-000000000002', 'Test Bob', 'test.bob@example.com', '$2a$12$JozoqAf/8G70zk/crcoJJ./jKpPU9Zo0NP52CRgEAB0bgIUG8s2gi'),
                                                  ('72222222-bbbb-4b2b-9000-000000000003', 'Test Carol', 'test.carol@example.com', '$2a$12$JozoqAf/8G70zk/crcoJJ./jKpPU9Zo0NP52CRgEAB0bgIUG8s2gi')
ON CONFLICT (email) DO NOTHING;


-- Favorite genres - avoid duplicates via WHERE NOT EXISTS (no unique constraint on table)
DELETE FROM users_favorite_genres WHERE user_id IN (
  '72222222-bbbb-4b2b-9000-000000000001',
  '72222222-bbbb-4b2b-9000-000000000002',
  '72222222-bbbb-4b2b-9000-000000000003'
);

INSERT INTO users_favorite_genres (user_id, favorite_genres_id)
SELECT '72222222-bbbb-4b2b-9000-000000000001', (SELECT id FROM genres WHERE name = 'Science Fiction')
WHERE NOT EXISTS (
  SELECT 1 FROM users_favorite_genres ufg
  WHERE ufg.user_id = '72222222-bbbb-4b2b-9000-000000000001'
    AND ufg.favorite_genres_id = (SELECT id FROM genres WHERE name = 'Science Fiction')
);

INSERT INTO users_favorite_genres (user_id, favorite_genres_id)
SELECT '72222222-bbbb-4b2b-9000-000000000002', (SELECT id FROM genres WHERE name = 'Action')
WHERE NOT EXISTS (
  SELECT 1 FROM users_favorite_genres ufg
  WHERE ufg.user_id = '72222222-bbbb-4b2b-9000-000000000002'
    AND ufg.favorite_genres_id = (SELECT id FROM genres WHERE name = 'Action')
);

INSERT INTO users_favorite_genres (user_id, favorite_genres_id)
SELECT '72222222-bbbb-4b2b-9000-000000000003', (SELECT id FROM genres WHERE name = 'Drama')
WHERE NOT EXISTS (
  SELECT 1 FROM users_favorite_genres ufg
  WHERE ufg.user_id = '72222222-bbbb-4b2b-9000-000000000003'
    AND ufg.favorite_genres_id = (SELECT id FROM genres WHERE name = 'Drama')
);

-- VHS (5) - PK(id)
INSERT INTO vhs (id, title, release_date, genre_id, rental_price, stock_level, image_id, image_extension, status) VALUES
  ('73333333-cccc-4c3c-a000-000000000001', 'Test Action Tape', '2005-05-10 00:00:00+00', (SELECT id FROM genres WHERE name = 'Action'), 4.0, 3, NULL, NULL, 'AVAILABLE'),
  ('73333333-cccc-4c3c-a000-000000000002', 'Quiet Drama',      '2010-11-21 00:00:00+00', (SELECT id FROM genres WHERE name = 'Drama'), 3.3, 2, NULL, NULL, 'AVAILABLE'),
  ('73333333-cccc-4c3c-a000-000000000003', 'Laugh Lines',      '2007-04-07 00:00:00+00', (SELECT id FROM genres WHERE name = 'Comedy'), 2.5, 4, NULL, NULL, 'AVAILABLE'),
  ('73333333-cccc-4c3c-a000-000000000004', 'Retro Galaxy',     '1999-07-16 00:00:00+00', (SELECT id FROM genres WHERE name = 'Science Fiction'), 6.5, 2, NULL, NULL, 'AVAILABLE'),
  ('73333333-cccc-4c3c-a000-000000000005', 'Action Reloaded',  '2015-03-14 00:00:00+00', (SELECT id FROM genres WHERE name = 'Action'), 7.8, 1, NULL, NULL, 'AVAILABLE')
ON CONFLICT (id) DO NOTHING;

-- Rentals (8) - PK(id)
INSERT INTO rentals (id, vhs_id, user_id, rental_date, due_date, return_date, price, version) VALUES
                                                                                                  ('75555555-eeee-4e5e-c000-000000000001', '73333333-cccc-4c3c-a000-000000000001', '72222222-bbbb-4b2b-9000-000000000001', '2025-01-05 10:00:00+00', '2025-01-07 10:00:00+00', '2025-01-07 09:00:00+00', 8.00, 0),
                                                                                                  ('75555555-eeee-4e5e-c000-000000000002', '73333333-cccc-4c3c-a000-000000000002', '72222222-bbbb-4b2b-9000-000000000001', '2025-01-10 12:00:00+00', '2025-01-12 12:00:00+00', '2025-01-12 10:00:00+00', 6.60, 0),
                                                                                                  ('75555555-eeee-4e5e-c000-000000000003', '73333333-cccc-4c3c-a000-000000000003', '72222222-bbbb-4b2b-9000-000000000002', '2025-01-15 09:00:00+00', '2025-01-17 09:00:00+00', '2025-01-17 08:00:00+00', 5.00, 0),
                                                                                                  ('75555555-eeee-4e5e-c000-000000000004', '73333333-cccc-4c3c-a000-000000000004', '72222222-bbbb-4b2b-9000-000000000002', '2025-02-02 14:00:00+00', '2025-02-04 14:00:00+00', '2025-02-04 13:00:00+00', 13.00, 0),
                                                                                                  ('75555555-eeee-4e5e-c000-000000000005', '73333333-cccc-4c3c-a000-000000000005', '72222222-bbbb-4b2b-9000-000000000003', '2025-02-10 10:00:00+00', '2025-02-12 10:00:00+00', NULL, NULL, 0),
                                                                                                  ('75555555-eeee-4e5e-c000-000000000006', '73333333-cccc-4c3c-a000-000000000003', '72222222-bbbb-4b2b-9000-000000000003', '2025-02-12 11:00:00+00', '2025-02-14 11:00:00+00', NULL, NULL, 0),
                                                                                                  ('75555555-eeee-4e5e-c000-000000000007', '73333333-cccc-4c3c-a000-000000000001', '72222222-bbbb-4b2b-9000-000000000002', '2025-02-14 12:00:00+00', '2025-02-16 12:00:00+00', NULL, NULL, 0),
                                                                                                  ('75555555-eeee-4e5e-c000-000000000008', '73333333-cccc-4c3c-a000-000000000002', '72222222-bbbb-4b2b-9000-000000000003', '2025-02-16 13:00:00+00', '2025-02-18 13:00:00+00', NULL, NULL, 0)
ON CONFLICT (id) DO NOTHING;

-- Reviews (6) - PK(id), rental_id UNIQUE
INSERT INTO reviews (id, rental_id, rating, comment) VALUES
  ('76666666-ffff-4f6f-d000-000000000001', '75555555-eeee-4e5e-c000-000000000001', 4.0, 'Solid action, good pacing.'),
  ('76666666-ffff-4f6f-d000-000000000002', '75555555-eeee-4e5e-c000-000000000002', 3.5, 'Quiet but engaging drama.'),
  ('76666666-ffff-4f6f-d000-000000000003', '75555555-eeee-4e5e-c000-000000000003', 4.2, 'Funny and lighthearted.'),
  ('76666666-ffff-4f6f-d000-000000000004', '75555555-eeee-4e5e-c000-000000000004', 4.5, 'Classic sci-fi feel.'),
  ('76666666-ffff-4f6f-d000-000000000005', '75555555-eeee-4e5e-c000-000000000005', 3.8, 'Suspenseful and engaging.'),
  ('76666666-ffff-4f6f-d000-000000000006', '75555555-eeee-4e5e-c000-000000000006', 3.9, 'Great performances.')
ON CONFLICT (id) DO NOTHING;
