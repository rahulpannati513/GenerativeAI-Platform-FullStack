import { useState } from "react";
import axios from "axios";

const AudioUploader = () => {
  const [file, setFile] = useState();
  const [transcription, setTranscription] = useState("");

  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
  };

  const handleUpload = async () => {
    const formData = new FormData();
    formData.append("file", file);

    const apiBaseUrl = import.meta.env.VITE_API_BASE_URL;
    const apiEndpoint = "api/v1/send-MultiLangAudio";
    const apiUrl = apiBaseUrl + apiEndpoint;

    try {
      const response = await axios.post(apiUrl, formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });
      setTranscription(response.data);
      console.log("Transcription", response.data);
    } catch (error) {
      console.error("Error Transcribing audio", error);
    }
  };

  return (
    <div className="bg-custom-bg text-custom-text min-h-screen flex justify-center items-center">
      <div className="flex flex-col items-center w-full sm:w-3/4 md:w-2/3 lg:w-1/2 xl:w-1/3 p-4">
        <h1 className="text-4xl sm:text-5xl md:text-6xl font-bold text-center mb-6">
          Audio To Text Transcriber
        </h1>

        {/* File Upload Section */}
        <input
          className="w-full sm:w-auto p-2 text-lg border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 mb-6"
          type="file"
          accept="audio/*"
          onChange={handleFileChange}
        />

        {/* Upload Button */}
        <button
          onClick={handleUpload}
          className="w-full sm:w-auto text-xl bg-blue-500 hover:bg-blue-700 text-white py-2 px-4 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
        >
          Upload and Transcribe
        </button>

        {/* Transcription Result Section */}
        <div className="bg-[#2f2f2f] w-full sm:w-3/4 md:w-2/3 m-6 p-4 rounded-2xl mt-6">
          <h2 className="text-2xl sm:text-3xl text-center m-3 underline text-lime-50 text-opacity-85">
            Transcription Result
          </h2>
          <p className="text-base sm:text-xl text-gray-200 m-3 text-opacity-95 shadow-slate-100 break-words">
            {transcription || "Your transcription result will appear here."}
          </p>
        </div>
      </div>
    </div>
  );
};

export default AudioUploader;
