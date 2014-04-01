
INSERT INTO `companies` (`id`, `name`, `active`) VALUES
(1, 'Milestone Intelligence Group, Inc.', true);


INSERT INTO `users` (`id`, `company_id`, `login`, `hashed_pass`, `salt`, `email`, `first_name`, `last_name`, `active`) VALUES
(1, 1, 'mday', '438030a252b811c79dc0c9e9201eb37d5205a4cf223c264e77e81dac9ef4f3b0b2e5b27c56966dc4e1a51922b4aa35f542c1f3ddf08e9b513727750c0eb78429', 'salt', 'mday@arcblaze.com', 'Mike', 'Day', 1),
(2, 1, 'user', '438030a252b811c79dc0c9e9201eb37d5205a4cf223c264e77e81dac9ef4f3b0b2e5b27c56966dc4e1a51922b4aa35f542c1f3ddf08e9b513727750c0eb78429', 'salt', 'user@arcblaze.com', 'User', 'Last', 1);


INSERT INTO `roles` (`name`, `user_id`) VALUES
('ADMIN', 1);


INSERT INTO `supervisors` (`company_id`, `user_id`, `supervisor_id`, `is_primary`) VALUES
(1, 1, 2, 1),
(1, 2, 1, 1);


INSERT INTO `tasks` (`id`, `company_id`, `description`, `job_code`, `admin`, `active`) VALUES
(1, 1, 'Paid Time Off', 'ArcBlaze:PTO', 1, 1),
(2, 1, 'Leave Without Pay', 'ArcBlaze:LWOP', 1, 1),
(3, 1, 'Holiday', 'ArcBlaze:HOL', 1, 1),
(4, 1, 'Overhead', 'ArcBlaze:OH', 1, 1),
(5, 1, 'G&A', 'ArcBlaze:G&A', 1, 1),
(6, 1, 'Bid and Proposal', 'ArcBlaze:BP', 1, 1),
(7, 1, 'Jury Duty', 'ArcBlaze:JURY', 1, 1),
(8, 1, 'Bereavement', 'ArcBlaze:BER', 1, 1),
(9, 1, 'Example Task', 'Example:TASK1', 0, 1),
(10, 1, 'Another Task', 'Another:TASK2', 0, 1),
(11, 1, 'Third Task', 'Third:TASK3', 0, 1),
(12, 1, 'Inactive Task', 'Example:INACTIVE', 0, 0);


INSERT INTO `assignments` (`id`, `company_id`, `task_id`, `user_id`, `labor_cat`, `item_name`, `begin`, `end`) VALUES
(1, 1, 9, 1, 'LCAT1', 'Mike Day:Mike - TASK1 LCAT1', '2013-12-01', '2014-12-31'),
(2, 1, 9, 1, 'LCAT2', 'Mike Day:Mike - TASK1 LCAT2', '2013-12-01', '2014-12-31'),
(3, 1, 10, 1, 'LCAT', 'Mike Day:Mike - TASK2 LCAT', '2013-12-01', '2014-12-31'),
(4, 1, 11, 1, 'LCAT', 'Mike Day:Mike - TASK3 LCAT', '2013-12-20', '2013-12-29'),
(5, 1, 12, 1, 'LCAT', 'Mike Day:Mike - INACTIVE LCAT', '2013-12-01', '2014-12-31'),
(6, 1, 9, 2, 'LCAT1', 'Example Last:Example - TASK1 LCAT1', '2013-12-01', '2014-12-31');


INSERT INTO `pay_periods` (`company_id`, `begin`, `end`, `type`) VALUES
(1, '2013-11-16', '2013-11-30', 'SEMI_MONTHLY'),
(1, '2013-12-01', '2013-12-15', 'SEMI_MONTHLY'),
(1, '2013-12-16', '2013-12-31', 'SEMI_MONTHLY');


INSERT INTO `bills` (`id`, `assignment_id`, `task_id`, `user_id`, `day`, `hours`, `timestamp`) VALUES
(1, 1, 9, 1, '2013-12-20', '5.50', '2013-11-01 11:50:19'),
(2, 2, 9, 1, '2013-12-21', '2.50', '2013-11-01 12:26:06'),
(3, 3, 10, 1, '2013-12-22', '2.55', '2013-11-01 12:26:06'),
(4, 4, 11, 1, '2013-12-23', '5.00', '2013-11-01 12:26:06'),
(5, NULL, 11, 1, '2013-12-24', '8.00', '2013-11-01 12:26:06'),
(6, NULL, 3, 1, '2013-12-25', '8.00', '2013-11-01 12:37:00'),
(7, NULL, 1, 1, '2013-12-26', '8.00', '2013-11-01 12:37:00');


INSERT INTO `timesheets` (`id`, `company_id`, `user_id`, `pp_begin`, `completed`, `approved`, `verified`, `exported`, `approver_id`, `verifier_id`) VALUES
(1, 1, 1, '2013-12-16', 0, 0, 0, 0, NULL, NULL);


INSERT INTO `holidays` (`id`, `company_id`, `description`, `config`) VALUES
(1, 1, 'New Years', 'January 1st Observance'),
(2, 1, 'President''s Day', '3rd Monday in February'),
(3, 1, 'Memorial Day', 'Last Monday in May'),
(4, 1, 'Independence Day', 'July 4th Observance'),
(5, 1, 'Labor Day', '1st Monday in September'),
(6, 1, 'Columbus Day', '2nd Monday in October'),
(7, 1, 'Veterans Day', 'November 11th Observance'),
(8, 1, 'Thanksgiving Day', '4th Thursday in November'),
(9, 1, 'Christmas Day', 'December 25th Observance'),
(10, 1, 'Martin Luther King Junior Day', '3rd Monday in January'),
(11, 1, 'Friday After Thanksgiving', '4th Thursday in November + 1');

INSERT INTO transactions (`company_id`, `user_id`, `timestamp`, `type`, `description`, `amount`, `notes`) VALUES
(1, 1, '2013-12-24 01:02:03', 'PAYMENT', 'Purchased 40 user months.', '20.00', NULL);

