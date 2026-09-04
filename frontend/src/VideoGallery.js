import React, { useEffect, useState } from 'react';
import config from './config';
import './Gallery.css';

function VideoGallery() {
  const [videos, setVideos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedVideo, setSelectedVideo] = useState(null);

  useEffect(() => {
    fetch(`${config.API_BASE_URL}/api/videos`)
      .then(res => {
        if (!res.ok) throw new Error('Failed to load videos');
        return res.json();
      })
      .then(data => {
        setVideos(data);
        setLoading(false);
      })
      .catch(e => {
        setError(e.message);
        setLoading(false);
      });
  }, []);

  const openPlayer = (video) => setSelectedVideo(video);
  const closePlayer = () => setSelectedVideo(null);

  const handleOverlayClick = (e) => {
    if (e.target.classList.contains('modal-overlay')) {
      closePlayer();
    }
  };

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div className="gallery-container">
      <h2 className="gallery-title">Video Gallery</h2>
      <div className="gallery-grid">
        {videos.map(video => (
          <div key={video.id} className="gallery-card">
            <button
              type="button"
              className="video-thumb-btn"
              onClick={() => openPlayer(video)}
              aria-label={`Play ${video.title}`}
            >
              <img
                src={`${config.API_BASE_URL}${video.imageUrl}`}
                alt={video.title}
                className="gallery-image"
              />
              <span className="video-play-icon">
                <i className="fa-solid fa-play"></i>
              </span>
            </button>
            <div className="gallery-title-text">{video.title}</div>
            {video.creator && (
              <div className="gallery-subtitle">{video.creator}</div>
            )}
            {video.description && (
              <div className="gallery-subtitle">{video.description}</div>
            )}
            <button className="gallery-button" onClick={() => openPlayer(video)}>
              <i className="fa-solid fa-play"></i>
              Watch
            </button>
          </div>
        ))}
      </div>

      {selectedVideo && (
        <div className="modal-overlay" onClick={handleOverlayClick}>
          <div className="modal-content video-modal-content">
            <button className="modal-close" onClick={closePlayer} aria-label="Close">
              <i className="fa-solid fa-times"></i>
            </button>
            <video
              className="video-player"
              controls
              autoPlay
              playsInline
              poster={`${config.API_BASE_URL}${selectedVideo.imageUrl}`}
              src={`${config.API_BASE_URL}${selectedVideo.downloadUrl}`}
            >
              Your browser does not support the video tag.
            </video>
            <div className="modal-info">
              <h3>{selectedVideo.title}</h3>
              {selectedVideo.creator && (
                <p className="modal-artist">{selectedVideo.creator}</p>
              )}
              {selectedVideo.description && (
                <p>{selectedVideo.description}</p>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default VideoGallery;
