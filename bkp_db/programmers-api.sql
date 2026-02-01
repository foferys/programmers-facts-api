-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Creato il: Dic 06, 2024 alle 15:27
-- Versione del server: 10.4.28-MariaDB
-- Versione PHP: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `programmers-api`
--

-- --------------------------------------------------------

--
-- Struttura della tabella `phrases`
--

CREATE TABLE `phrases` (
  `id` int(11) NOT NULL,
  `phrase` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dump dei dati per la tabella `phrases`
--

INSERT INTO `phrases` (`id`, `phrase`, `type`) VALUES
(1, 'Backend developers always say, \"It worked on my local server.\"', 'backend'),
(2, 'Frontend is 90% making things pretty and 10% crying over browser compatibility.', 'frontend'),
(3, 'Generic coding advice: if it works, don’t ask why.', 'generic'),
(4, 'The backend is like a restaurant kitchen: no one sees it, but it’s where the magic happens.', 'backend'),
(5, 'Frontend without animations is like pizza without cheese.', 'frontend'),
(6, 'The only thing consistent in programming is inconsistency.', 'generic'),
(7, 'Backend developers can solve any problem, as long as it doesn’t involve CSS.', 'backend'),
(8, 'Frontend developers always say, \"It looked fine on my machine.\"', 'frontend'),
(9, 'The golden rule of programming: If it’s working, don’t touch it.', 'generic'),
(10, 'Backend errors are like ghosts; they appear at night and disappear when you call for help.', 'backend'),
(11, 'A frontend developer’s worst nightmare? A client saying, \"Can we make it pop more?\"', 'frontend'),
(12, 'Programming is 1% writing code and 99% figuring out what you wrote last week.', 'generic'),
(13, 'Backend developers hate \"quick fixes\" as much as cats hate baths.', 'backend'),
(14, 'Frontend developers speak fluent \"hexadecimal.\"', 'frontend'),
(15, 'Programming is the art of turning coffee into stack traces.', 'generic'),
(16, 'Backend developers don’t fear downtime; they fear “urgent deployments.”', 'backend'),
(17, 'Frontend debugging is like playing hide and seek with invisible errors.', 'frontend'),
(18, 'The real enemy in programming is the \"off by one\" error.', 'generic'),
(19, 'Backend developers believe in one god: the database.', 'backend'),
(20, 'Frontend without JavaScript is like a car without wheels.', 'frontend'),
(21, 'The most dangerous phrase in programming: \"This will only take a minute.\"', 'generic'),
(22, 'Backend developers know that the fastest query is the one you don’t run.', 'backend'),
(23, 'Frontend developers measure success in pixels and frustration.', 'frontend'),
(24, 'The first rule of debugging: Don’t make it worse.', 'generic'),
(25, 'Backend developers are the unsung heroes of 404 errors.', 'backend'),
(26, 'Frontend work is just coloring books for adults, right?', 'frontend'),
(27, 'The longer the comment, the less sense the code makes.', 'generic'),
(28, 'Backend developers are allergic to \"non-technical explanations.\"', 'backend'),
(29, 'Frontend developers fear only one thing: Internet Explorer.', 'frontend'),
(30, 'Programmers write code like poets write sonnets – but with more tears.', 'generic'),
(31, 'Backend developers think \"scaling\" is just a fancy word for \"good luck.\"', 'backend'),
(32, 'Frontend developers love SVGs, until they have to edit them.', 'frontend'),
(33, 'Programming is just a series of compromises wrapped in semicolons.', 'generic'),
(34, 'Backend errors always appear five minutes before a deadline.', 'backend'),
(35, 'Frontend developers dream in gradients and shadows.', 'frontend'),
(36, 'The only constant in programming is the ever-changing requirements.', 'generic'),
(37, 'Backend developers call their databases “a work in progress.”', 'backend'),
(38, 'Frontend developers think CSS Grid is both a gift and a curse.', 'frontend'),
(39, 'Programming is like trying to explain algebra to a rock.', 'generic'),
(40, 'Backend developers see APIs as their love letters to the frontend.', 'backend'),
(41, 'Frontend development is 50% design and 50% Google.', 'frontend'),
(42, 'The best code is no code, said no programmer ever.', 'generic'),
(43, 'Backend developers trust logs more than people.', 'backend'),
(44, 'Frontend developers call CSS bugs \"happy little accidents.\"', 'frontend'),
(45, 'Programming languages are like relationships: complicated and full of compromises.', 'generic'),
(46, 'Backend developers believe in one thing: “It’s not my fault.”', 'backend'),
(47, 'Frontend developers can center a div… eventually.', 'frontend'),
(48, 'Programming is proof that chaos can be organized – sometimes.', 'generic'),
(49, 'Backend developers love unit tests, until they have to write them.', 'backend'),
(50, 'Frontend developers never sleep; they just debug CSS.', 'frontend'),
(51, 'Programming without bugs is like an ocean without water.', 'generic'),
(52, 'Backend developers know that \"optimization\" is just another word for pain.', 'backend'),
(53, 'Frontend developers know that no two browsers are ever the same.', 'frontend'),
(54, 'Programming is the act of creating problems from solutions.', 'generic'),
(55, 'Backend developers live for that one perfect query.', 'backend'),
(56, 'Frontend developers are wizards of pixel perfection.', 'frontend'),
(57, 'The hardest part of programming is explaining it to someone else.', 'generic'),
(58, 'Backend developers secretly enjoy 500 errors – as long as they’re not theirs.', 'backend'),
(59, 'Frontend developers consider animations an art form.', 'frontend'),
(60, 'Programming is 90% frustration and 10% eureka moments.', 'generic'),
(61, 'Backend developers see every bug as a “learning opportunity.”', 'backend'),
(62, 'Frontend developers know that perfection is unattainable, but they still try.', 'frontend'),
(63, 'Programming is just glorified typing.', 'generic'),
(64, 'Backend developers joke that \"infinity\" is just their query response time.', 'backend'),
(65, 'Frontend developers fight a daily battle with flexbox.', 'frontend'),
(66, 'The only way to fix a bug is to create two more.', 'generic'),
(67, 'Backend developers consider downtime a personal insult.', 'backend'),
(68, 'Frontend developers think \"responsive design\" is a never-ending saga.', 'frontend'),
(69, 'Programming is a constant search for the perfect variable name.', 'generic'),
(70, 'Backend developers write code that no one sees but everyone relies on.', 'backend'),
(71, 'Frontend developers wonder if CSS will ever be \"done.\"', 'frontend'),
(72, 'Programming is debugging your own genius.', 'generic'),
(73, 'Backend developers love scaling, until they have to pay for it.', 'backend'),
(74, 'Frontend developers laugh in the face of JavaScript errors – and then cry.', 'frontend'),
(75, 'Programming is turning complexity into simplicity, one bug at a time.', 'generic'),
(76, 'Backend developers think “optimized” is just code for “too late.”', 'backend'),
(77, 'Frontend developers know that “modern design” changes every week.', 'frontend'),
(78, 'Programming is like cooking without a recipe, but with extra fire.', 'generic'),
(79, 'Backend developers know the true cost of \"one more feature.\"', 'backend'),
(80, 'Frontend developers see the world in HEX and RGB.', 'frontend'),
(81, 'Programming is solving puzzles you didn’t know existed.', 'generic'),
(82, 'Backend developers think in schemas, not sentences.', 'backend'),
(83, 'Frontend developers dream of perfectly aligned divs.', 'frontend'),
(84, 'The hardest bug to fix is the one you didn’t create.', 'generic'),
(85, 'Backend developers believe \"legacy code\" is a fancy term for ancient ruins.', 'backend'),
(86, 'Frontend developers see a margin problem and call it art.', 'frontend'),
(87, 'Programming is 1% inspiration and 99% stack overflow.', 'generic'),
(88, 'Backend developers prefer JSON over small talk.', 'backend'),
(89, 'Frontend developers use “pixel-perfect” as a battle cry.', 'frontend'),
(90, 'Programming is turning caffeine into code and stress.', 'generic'),
(91, 'Backend developers write APIs like poets write sonnets.', 'backend'),
(92, 'Frontend developers think every website is a masterpiece in progress.', 'frontend'),
(93, 'Programming is creating chaos with logic.', 'generic'),
(94, 'Backend developers know the struggle of “too many connections.”', 'backend'),
(95, 'Frontend developers see gradients where others see colors.', 'frontend'),
(96, 'The best programmer is the one who can explain it to a rubber duck.', 'generic'),
(97, 'Backend developers fear only one thing: \"The client wants this tomorrow.\"', 'backend'),
(98, 'Frontend developers celebrate every browser update like a national holiday.', 'frontend'),
(99, 'Programming is debugging the universe, one line at a time.', 'generic'),
(100, 'Backend developers think servers are sacred, except when they crash.', 'backend');

--
-- Indici per le tabelle scaricate
--

--
-- Indici per le tabelle `phrases`
--
ALTER TABLE `phrases`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT per le tabelle scaricate
--

--
-- AUTO_INCREMENT per la tabella `phrases`
--
ALTER TABLE `phrases`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=101;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
