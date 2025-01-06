import { useState } from "react";
import { Link } from "react-router-dom";

function Header() {
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  const toggleMenu = () => {
    setIsMenuOpen((prevState) => !prevState);
  };

  return (
    <header className="bg-gradient-to-r from-[#2A2A2A] to-[#1D1D1D] p-4 shadow-lg">
      <div className="max-w-7xl mx-auto flex justify-between items-center">
        {/* Logo / App Name */}
        <div className="text-white text-3xl font-bold">
          <Link
            to="/"
            className="hover:text-teal-500 opacity-70 transition duration-300"
          >
            My AI Assistant
          </Link>
        </div>

        {/* Hamburger Icon for mobile */}
        <div className="lg:hidden">
          <button
            onClick={toggleMenu}
            className="text-white focus:outline-none"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              className="h-6 w-6"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
              strokeWidth="2"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                d="M4 6h16M4 12h16M4 18h16"
              />
            </svg>
          </button>
        </div>

        {/* Navigation Menu (Desktop) */}
        <nav className="hidden lg:flex items-center space-x-8">
          <ul className="flex space-x-8">
            <li>
              <Link
                to="/"
                className="text-white opacity-70 text-lg hover:text-teal-500 transition duration-300"
              >
                Home
              </Link>
            </li>
            <li>
              <Link
                to="/image"
                className="text-white opacity-70 text-lg hover:text-teal-500 transition duration-300"
              >
                Image Generation
              </Link>
            </li>
            <li>
              <Link
                to="/audio"
                className="text-white opacity-70 text-lg hover:text-teal-500 transition duration-300"
              >
                Audio-to-Text
              </Link>
            </li>
            <li>
              <Link
                to="/recipe"
                className="text-white opacity-70 text-lg hover:text-teal-500 transition duration-300"
              >
                Recipe Generator
              </Link>
            </li>
            <li>
              <Link
                to="/aboutDeveloper"
                className="text-white opacity-70 text-lg hover:text-teal-500 transition duration-300"
              >
                About Developer
              </Link>
            </li>
          </ul>
        </nav>
      </div>

      {/* Mobile Menu for small screens */}
      <div
        className={`lg:hidden transition-all duration-300 ease-in-out ${
          isMenuOpen ? "block" : "hidden"
        }`}
      >
        <ul className="space-y-4 bg-gradient-to-r from-[#2A2A2A] to-[#1D1D1D] p-4">
          <li>
            <Link
              to="/"
              className="text-white opacity-70 text-lg hover:text-teal-500 transition duration-300"
              onClick={() => setIsMenuOpen(false)}
            >
              Home
            </Link>
          </li>
          <li>
            <Link
              to="/image"
              className="text-white opacity-70 text-lg hover:text-teal-500 transition duration-300"
              onClick={() => setIsMenuOpen(false)}
            >
              Image Generation
            </Link>
          </li>
          <li>
            <Link
              to="/audio"
              className="text-white opacity-70 text-lg hover:text-teal-500 transition duration-300"
              onClick={() => setIsMenuOpen(false)}
            >
              Audio-to-Text
            </Link>
          </li>
          <li>
            <Link
              to="/recipe"
              className="text-white opacity-70 text-lg hover:text-teal-500 transition duration-300"
              onClick={() => setIsMenuOpen(false)}
            >
              Recipe Generator
            </Link>
          </li>
          <li>
            <Link
              to="/aboutDeveloper"
              className="text-white opacity-70 text-lg hover:text-teal-500 transition duration-300"
              onClick={() => setIsMenuOpen(false)}
            >
              About Developer
            </Link>
          </li>
        </ul>
      </div>
    </header>
  );
}

export default Header;
