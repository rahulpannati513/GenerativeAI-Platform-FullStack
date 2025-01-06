import { useState, useEffect, useRef } from "react";
import axios from "axios";

const ImageUploader = () => {
  const [message, setMessage] = useState("");
  const [imageUrl, setImageUrl] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [imageLoading, setImageLoading] = useState(true);
  const [showGuidelines, setShowGuidelines] = useState(false);
  const guidelinesRef = useRef(null);

  // Handle changes in the message input
  const handleInputChange = (e) => {
    setMessage(e.target.value);
  };

  // Generate image based on user description
  const handleGenerateImage = async () => {
    if (!message.trim()) {
      setError("Please enter a description to generate an image.");
      return;
    }

    setLoading(true);
    setError("");
    setImageUrl(""); // Reset image URL to hide previous images
    const apiEndpoint = "/api/v1/generate-image";
    const apiUrl = import.meta.env.VITE_API_BASE_URL + apiEndpoint;

    try {
      const response = await axios.get(apiUrl, {
        params: { message: message },
      });

      // Adjust this to match the actual response structure of your API
      const generatedImageUrl = response?.data?.result?.output?.url || "";

      if (generatedImageUrl) {
        setImageUrl(generatedImageUrl);
        setImageLoading(true); // Reset image loading state when new image is fetched
      } else {
        setError("Failed to generate an image. Please try again.");
      }

      setLoading(false);
    } catch (err) {
      setError("Error occurred while generating image. Please try again.");
      setLoading(false);
    }
  };

  // Image load handler to hide the loading spinner once the image is loaded
  const handleImageLoad = () => {
    setImageLoading(false);
  };

  // Download the generated image
  const downloadImage = async (e) => {
    e.preventDefault();
    const imageUrl = e.target.href;

    try {
      const response = await fetch(imageUrl);
      const buffer = await response.arrayBuffer();
      const blob = new Blob([buffer]);
      const downloadUrl = window.URL.createObjectURL(blob);

      const link = document.createElement("a");
      link.href = downloadUrl;
      link.setAttribute("download", "generated-image.png");
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    } catch (err) {
      console.error("Error downloading image:", err);
    }
  };

  // Close the guidelines if clicked outside the guidelines box
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (
        guidelinesRef.current &&
        !guidelinesRef.current.contains(event.target)
      ) {
        setShowGuidelines(false); // Close guidelines if clicked outside
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside); // Cleanup the event listener
    };
  }, []);

  return (
    <div className="relative bg-gradient-to-r from-gray-800 via-gray-900 to-gray-900 text-white min-h-screen flex justify-center items-center">
      {/* Guidelines Section - Positioned at top left */}
      {showGuidelines && (
        <div
          ref={guidelinesRef}
          className="absolute top-0 left-0 m-4 bg-gray-800 text-white p-4 rounded-lg shadow-lg w-[300px] z-50"
        >
          <h3 className="text-2xl font-semibold">Guidelines</h3>
          <ul className="list-disc pl-6 text-lg mt-4">
            <li>
              Provide clear, concise descriptions for accurate image generation.
            </li>
            <li>Avoid describing harmful or offensive content.</li>
            <li>Use creative words to generate unique images.</li>
            <li>
              Keep your descriptions under 150 words for best performance.
            </li>
            <li>
              Images generated may not always match your expectations, but be
              creative!
            </li>
          </ul>
        </div>
      )}

      {/* Toggle Button for Guidelines */}
      <button
        onClick={() => setShowGuidelines(!showGuidelines)}
        className="absolute top-6 left-6 text-lg text-indigo-400 hover:text-indigo-600"
      >
        {showGuidelines ? "Hide Guidelines" : "View Guidelines"}
      </button>

      <div className="flex flex-col lg:flex-row w-full max-w-7xl px-4 sm:px-6 md:px-8 py-8 space-y-6 lg:space-y-0 lg:space-x-8">
        {/* Left Side - Input Form */}
        <div className="flex flex-col w-full lg:w-2/5 bg-gray-800 bg-opacity-80 p-6 rounded-xl shadow-lg">
          <h1 className="text-3xl sm:text-4xl font-bold text-center mb-6 sm:mb-8">
            AI-Driven Image Generation
          </h1>
          <p className="text-lg sm:text-xl text-center mb-6 sm:mb-8">
            Transform your text descriptions into stunning visuals in seconds.
          </p>
          <textarea
            className="w-full p-4 text-xl text-gray-900 border-none rounded-lg shadow-xl bg-gray-700 bg-opacity-80 focus:outline-none focus:ring-2 focus:ring-indigo-500 mb-6"
            placeholder="Describe the image you want to generate..."
            value={message}
            onChange={handleInputChange}
            rows="6"
          ></textarea>
          <button
            onClick={handleGenerateImage}
            className="w-full sm:w-auto text-xl bg-indigo-600 hover:bg-indigo-700 text-white py-4 px-8 rounded-lg focus:outline-none focus:ring-2 focus:ring-indigo-500"
            disabled={loading} // Disable button while loading
          >
            {loading ? "Generating..." : "Generate Image"}
          </button>

          {error && (
            <div className="mt-4 text-red-600 font-semibold bg-red-100 p-4 rounded-lg shadow-lg">
              <p>{error}</p>
            </div>
          )}
        </div>

        {/* Right Side - Image Display */}
        <div className="flex flex-col w-full lg:w-3/5 bg-gray-800 bg-opacity-80 p-6 rounded-xl shadow-lg">
          <h3 className="text-2xl text-center mb-6">Your Generated Image</h3>
          {loading && (
            <div className="flex justify-center items-center h-full w-full bg-gray-700 rounded-lg shadow-xl p-6">
              <div className="animate-spin rounded-full border-4 border-t-4 border-gray-400 w-16 h-16 mb-4"></div>
              <p className="text-white text-lg">Generating...</p>
            </div>
          )}

          {imageUrl && !loading && (
            <div className="mt-6 w-full bg-gray-700 bg-opacity-60 p-6 rounded-lg shadow-lg overflow-hidden">
              <img
                src={imageUrl}
                alt="Generated"
                onLoad={handleImageLoad}
                className={`w-full max-h-[400px] object-cover rounded-lg shadow-xl ${
                  imageLoading ? "opacity-0" : "opacity-100"
                } transition-opacity duration-500`}
              />
              <a
                href={imageUrl}
                download
                onClick={downloadImage}
                className="mt-6 inline-block text-xl py-3 px-8 bg-gradient-to-r from-indigo-600 to-indigo-400 text-white rounded-full shadow-lg hover:bg-indigo-500 transform hover:scale-105 transition-all duration-300"
              >
                Download Image
              </a>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default ImageUploader;
