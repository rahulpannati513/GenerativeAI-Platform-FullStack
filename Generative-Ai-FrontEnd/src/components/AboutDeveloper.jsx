const AboutDeveloper = () => {
  return (
    <div className="bg-[#111111] bg-opacity-95 flex justify-center items-center p-8">
      <div className="max-w-5xl mx-auto bg-transparent p-12 rounded-3xl shadow-2xl overflow-hidden flex flex-col md:flex-row space-y-10 md:space-y-0 md:space-x-16">
        {/* Left Section: Profile Image, Name, and Social Links */}
        <div className="flex flex-col items-center text-center md:items-start md:text-left space-y-8 md:w-1/3">
          {/* Profile Picture */}
          <img
            src="https://media.licdn.com/dms/image/v2/C5103AQHkhR-JfvTYgg/profile-displayphoto-shrink_800_800/profile-displayphoto-shrink_800_800/0/1573198501512?e=1738195200&v=beta&t=RoUoxK404abrTScijG8_EwDST80MEv2lRsvhfZIV9Yg"
            alt="Developer Profile"
            className="w-40 h-40 rounded-full border-8 border-gradient-to-r from-pink-600 to-blue-500 mb-6 shadow-lg"
          />
          <h2 className="text-4xl font-bold text-white">Rahul Pannati</h2>
          <p className="text-lg text-gray-300">
            Software Developer | AI & ML Enthusiast | Full-stack Web Developer
          </p>

          {/* Social Media Links */}
          <div className="flex space-x-6 mt-6 text-white text-3xl">
            {/* LinkedIn Link */}
            <a
              href="https://www.linkedin.com/in/rahul-pannati-599335197"
              target="_blank"
              rel="noopener noreferrer"
              className="hover:opacity-80 transition duration-300"
            >
              <img
                src="https://content.linkedin.com/content/dam/me/about/LinkedIn_Icon.jpg.original.jpg"
                alt="LinkedIn"
                className="w-8 h-8"
              />
            </a>

            {/* GitHub Link */}
            <a
              href="https://github.com/rahulpannati513"
              target="_blank"
              rel="noopener noreferrer"
              className="hover:opacity-80 transition duration-300"
            >
              <img
                src="https://upload.wikimedia.org/wikipedia/commons/c/c2/GitHub_Invertocat_Logo.svg"
                alt="GitHub"
                className="w-8 h-8"
              />
            </a>

            {/* Portfolio Link */}
            <a
              href="https://rahulpannati.dev"
              target="_blank"
              rel="noopener noreferrer"
              className="hover:opacity-80 transition duration-300"
            >
              <img
                src="https://flowcv.com/favicon.svg"
                alt="Portfolio"
                className="w-8 h-8"
              />
            </a>

            {/* Email Link */}
            <a
              href="mailto:rahulpannati1@gmail.com"
              className="hover:opacity-80 transition duration-300"
            >
              <img
                src="https://upload.wikimedia.org/wikipedia/commons/7/7e/Gmail_icon_%282020%29.svg"
                alt="Email"
                className="w-8 h-8"
              />
            </a>
          </div>

          {/* Download Resume Section (on the left, below LinkedIn) */}
          <div className="mt-6 flex flex-col items-center md:items-start">
            <a
              href="https://github.com/rahulpannati513/resume/raw/refs/heads/main/Rahul_Backend_1728286078179_Rahul%20Pannati.pdf" // Replace with actual file path or URL
              download
              className="inline-block bg-red-600 bg-opacity-40 text-white text-lg font-semibold py-3 px-6 rounded-full shadow-lg hover:opacity-80 transition duration-300"
            >
              Download Resume
            </a>
          </div>
        </div>

        {/* Right Section: About Me, Technologies, and Contact */}
        <div className="md:w-2/3 space-y-10">
          <h3 className="text-3xl font-semibold text-white">About Me</h3>
          <p className="text-lg text-gray-200">
            I&apos;m a passionate software developer with a deep interest in
            creating innovative and efficient solutions. I specialize in
            building full-stack web applications using modern technologies. I am
            particularly enthusiastic about AI, machine learning, and exploring
            the endless possibilities of web development.
          </p>

          <h3 className="text-3xl font-semibold text-white">Technologies</h3>
          <p className="text-lg text-gray-200">
            I work with a range of technologies, including:
            <ul className="list-disc pl-6 mt-2 text-gray-300">
              <li>React.js, JavaScript</li>
              <li>Spring Boot</li>
              <li>Tailwind CSS, Bootstrap</li>
            </ul>
          </p>

          <h3 className="text-3xl font-semibold text-white">Contact Me</h3>
          <p className="text-lg text-gray-200">
            Feel free to reach out if you have any questions or would like to
            collaborate on a project! I&apos;m always excited to connect with
            like-minded individuals and work on interesting challenges.
          </p>
        </div>
      </div>
    </div>
  );
};

export default AboutDeveloper;
