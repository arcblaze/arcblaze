
INSERT INTO `users` (`id`, `company_id`, `login`, `hashed_pass`, `salt`, `email`, `first_name`, `last_name`, `active`) VALUES
(1, 'mday', '438030a252b811c79dc0c9e9201eb37d5205a4cf223c264e77e81dac9ef4f3b0b2e5b27c56966dc4e1a51922b4aa35f542c1f3ddf08e9b513727750c0eb78429', 'salt', 'mday@arcblaze.com', 'Mike', 'Day', 1),
(2, 'user', '438030a252b811c79dc0c9e9201eb37d5205a4cf223c264e77e81dac9ef4f3b0b2e5b27c56966dc4e1a51922b4aa35f542c1f3ddf08e9b513727750c0eb78429', 'salt', 'user@arcblaze.com', 'User', 'Last', 1);


INSERT INTO `roles` (`name`, `user_id`) VALUES
('ADMIN', 1);


INSERT INTO transactions (`company_id`, `user_id`, `timestamp`, `type`, `description`, `amount`, `notes`) VALUES
(1, 1, '2013-12-24 01:02:03', 'PAYMENT', 'Purchased 40 user months.', '20.00', NULL);
