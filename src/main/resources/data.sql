-- Insert Roles
INSERT INTO roles (id, name) VALUES (1, 'ROLE_CUSTOMER') ON CONFLICT (id) DO NOTHING;
INSERT INTO roles (id, name) VALUES (2, 'ROLE_ADMIN') ON CONFLICT (id) DO NOTHING;



-- Insert Admin User
INSERT INTO users (id, first_name, last_name, email, password, created_at) VALUES
    (1, 'Admin', 'User', 'admin@geraldikem.com', '$2a$12$/1afRedFifGMgNkgzlk7EubDUNPz6.627RPf0mK1tIXhrabRiYfn.', NOW()) ON CONFLICT (id) DO NOTHING;

-- Link Admin User to Admin Role
INSERT INTO user_roles (user_id, role_id) VALUES (1, 2) ON CONFLICT (user_id, role_id) DO NOTHING;

INSERT INTO products (name, description, price, stock_quantity, material, dimensions, weight, is_featured, estimated_delivery_days, created_at) VALUES
-- Desk Accessories (8)
('Oakwood Desk Organizer', 'Keep your desk tidy with this handcrafted solid oak organizer, featuring compartments for pens, notes, and your phone.', 45.00, 25, 'Solid Oak Wood', '30cm x 15cm x 5cm', '0.8kg', true, 5, NOW()),
('Birchwood Pen Holder', 'A simple and sturdy pen and pencil holder, crafted from beautiful birchwood to keep your writing tools organized.', 18.00, 60, 'Solid Birchwood', '8cm x 8cm x 10cm', '0.3kg', false, 3, NOW()),
('Ergonomic Oak Laptop Stand', 'Elevate your workspace with this ergonomic laptop stand, designed to improve posture. Made from solid oak with natural cork grips.', 42.00, 20, 'Solid Oak Wood', '25cm x 28cm x 12cm', '0.7kg', true, 7, NOW()),
('Walnut Business Card Holder', 'Make a statement in your office with this sleek and minimalist walnut business card holder.', 22.00, 45, 'American Walnut', '10cm x 4cm x 5cm', '0.2kg', false, 4, NOW()),
('Maple Wood Tablet Stand', 'A sturdy and elegant stand for your tablet, handcrafted from light Canadian maple. Perfect for video calls or watching media.', 28.00, 35, 'Canadian Maple', '15cm x 12cm x 12cm', '0.4kg', false, 6, NOW()),
('Ash Wood Monitor Riser', 'Raise your monitor to a comfortable viewing height with this durable ash wood riser. Includes space underneath for keyboard storage.', 65.00, 18, 'Solid Ash Wood', '50cm x 22cm x 10cm', '1.5kg', true, 10, NOW()),
('Wooden Headphone Stand', 'A stylish and practical stand to store your headphones, keeping them safe and your desk organized.', 35.00, 30, 'Layered Birch Plywood', '12cm x 10cm x 25cm', '0.4kg', false, 5, NOW()),
('Minimalist Wooden Mousepad', 'A unique, smooth wooden mousepad with a cork base for non-slip performance. A beautiful alternative to traditional mousepads.', 29.00, 50, 'Sanded Cherry Wood', '22cm x 18cm x 0.5cm', '0.25kg', false, 3, NOW()),

-- Kitchenware (6)
('Walnut Coaster Set', 'A set of four elegant coasters, crafted from rich American walnut.', 25.00, 50, 'American Walnut', '10cm x 10cm x 1cm (each)', '0.2kg', true, 4, NOW()),
('Acacia Wood Serving Board', 'An elegant serving board ideal for cheeses, charcuterie, or appetizers. The natural acacia grain makes each board unique.', 38.00, 30, 'Acacia Wood', '40cm x 20cm x 2cm', '0.9kg', true, 6, NOW()),
('Olive Wood Salt & Pepper Grinders', 'A matching set of beautiful salt and pepper grinders made from richly grained olive wood.', 58.00, 22, 'Olive Wood', '6cm x 6cm x 15cm (each)', '0.6kg', false, 8, NOW()),
('Beech Wood Rolling Pin', 'A classic French-style tapered rolling pin made from solid beech wood for a smooth, non-stick surface.', 24.00, 40, 'Beech Wood', '50cm x 4.5cm', '0.5kg', false, 5, NOW()),
('Wooden Utensil Holder', 'Keep your kitchen tools organized with this spacious and sturdy wooden utensil holder.', 32.00, 33, 'Bamboo', '15cm x 15cm x 18cm', '0.7kg', false, 3, NOW()),
('Magnetic Knife Strip', 'A powerful magnetic knife strip made from acacia wood to stylishly and safely store your knives.', 48.00, 25, 'Acacia Wood & Neodymium Magnets', '45cm x 6cm x 2cm', '0.8kg', false, 7, NOW()),

-- Home Decor (6)
('Floating Walnut Shelf', 'A minimalist floating shelf made from a single piece of rich walnut, perfect for displaying books, plants, or decor.', 55.00, 15, 'Solid American Walnut', '60cm x 15cm x 4cm', '1.2kg', true, 12, NOW()),
('Geometric Wood Wall Art', 'A set of three geometric wall art pieces made from laser-cut birch plywood. A modern touch for any room.', 75.00, 12, 'Birch Plywood', '30cm x 30cm x 0.5cm (each)', '0.9kg', false, 10, NOW()),
('Set of 3 Wooden Planters', 'A trio of small, hexagonal wooden planters perfect for succulents or air plants. Made from pine.', 39.00, 28, 'Pine Wood', '10cm x 10cm x 8cm (each)', '0.6kg', false, 6, NOW()),
('Oak Photo Frames (Set of 3)', 'Display your favorite memories in this set of three solid oak photo frames for 4x6 pictures.', 49.00, 30, 'Solid Oak Wood', 'For 4x6 photos', '1.1kg', false, 5, NOW()),
('Driftwood Candle Holder', 'A rustic candle holder made from reclaimed driftwood, perfect for creating a cozy, coastal ambiance. Holds three tea lights.', 26.00, 40, 'Reclaimed Driftwood', 'Approx. 30cm long', '0.5kg', false, 14, NOW()),
('Wooden Wall-Mounted Coat Rack', 'A simple and functional coat rack with 5 oak pegs. Mounts easily to any wall.', 44.00, 24, 'Oak Wood', '60cm x 8cm x 6cm', '1.0kg', false, 8, NOW());

