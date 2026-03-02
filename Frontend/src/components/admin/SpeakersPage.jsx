import React, { useState, useEffect } from 'react';
import * as bootstrap from 'bootstrap'; 
import 'bootstrap/dist/css/bootstrap.min.css';
import { get, post, put, del } from '../../assets/utils/api.js';


const SpeakersPage = () => {
  const [speakers, setSpeakers] = useState([]);
  const [formData, setFormData] = useState({ name: '', bio: '', eventId: '' });
  const [selectedSpeaker, setSelectedSpeaker] = useState(null);
  const [selectedEvent, setSelectedEvent] = useState('');
  const [viewAll, setViewAll] = useState(false);


  useEffect(() => {
  if (viewAll) {
    fetchAllSpeakers();
  } else if (selectedEvent) {
    fetchSpeakers(selectedEvent);
  } else {
    fetchSpeakers(1); // default event ID
  }
}, [selectedEvent, viewAll]);

  const fetchAllSpeakers = async () => {
  try {
    //const response = await get('/speakers/event/All'); 
    const response = await get('/speakers/event/All');
    setSpeakers(response.data || []);
  } catch (error) {
    console.error('Error fetching all speakers:', error);
  }
};

  const fetchSpeakers = async (eventId) => {
    try {
      const response = await get(`/speakers/event/${eventId}`);
      console.log(response.data);
      const data = response.data.map(s => ({
       ...s,
      eventTitle: s.event?.title || '—'
      }));
        setSpeakers(data);

     // setSpeakers(response.data || []);
    } catch (error) {
      console.error('Error fetching speakers:', error);
    }
  };

  const openModal = (speaker = null) => {
    setSelectedSpeaker(speaker);
    setFormData(
      speaker
        ? { name: speaker.name, bio: speaker.bio, eventId: speaker.eventId }
        : { name: '', bio: '', eventId: '' }
    );
    const modalEl = document.getElementById('speakerModal');
    const modal = new bootstrap.Modal(modalEl);
    modal.show();
  };

  const closeModal = () => {
    const modalEl = document.getElementById('speakerModal');
    const modal = bootstrap.Modal.getInstance(modalEl);
    modal.hide();
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSaveSpeaker = async () => {
  try {
    if (selectedSpeaker) {
      // Update existing speaker using PUT
      await put(`/speakers/${selectedSpeaker.id}`, formData);
    } else {
      // Create new speaker using POST
      await post(`/speakers/event/${formData.eventId}`, formData);
    }
    await fetchSpeakers(formData.eventId);
    closeModal();
  } catch (error) {
    console.error('Error saving speaker:', error);
  }
};


  const handleDeleteSpeaker = async (id) => {
    if (!window.confirm('Are you sure you want to delete this speaker?')) return;
    try {
      await del(`/speakers/${id}`);
      await fetchSpeakers(formData.eventId || selectedEvent);
    } catch (error) {
      console.error('Error deleting speaker:', error);
    }
  };

  return (
    <div className="container mt-4">
      <h2>Speakers Management</h2>
      <button className="btn btn-success mb-3" onClick={() => openModal()}>
        Add Speaker
      </button>

      <div className="d-flex justify-content-between align-items-center mb-3">
         <h2>Speakers Management</h2>
        <div>
          <button
            className="btn btn-outline-primary me-2"
            onClick={() => setViewAll(!viewAll)}
          >
            {viewAll ? 'View Event Speakers' : 'View All Speakers'}
          </button>
        </div>
      </div>

      <table className="table table-bordered">
        <thead>
          <tr>
            <th>Event Name</th>
            <th>Name</th>
            <th>Bio</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {speakers.length > 0 ? (
            speakers.map((speaker) => (
              <tr key={speaker.id}>
                <td>{speaker.event?.title || '—'}</td>
                <td>{speaker.name}</td>
                <td>{speaker.bio}</td>
                <td>
                  <button
                    className="btn btn-sm btn-primary mb-1 me-2"
                    onClick={() => openModal(speaker)}
                  >
                    Edit
                  </button>
                  <button
                    className="btn btn-sm btn-danger"
                    onClick={() => handleDeleteSpeaker(speaker.id)}
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="3" className="text-center">
                No speakers found.
              </td>
            </tr>
          )}
        </tbody>
      </table>

      {/* Bootstrap Modal */}
      <div
        className="modal fade"
        id="speakerModal"
        tabIndex="-1"
        aria-labelledby="speakerModalLabel"
        aria-hidden="true"
      >
        <div className="modal-dialog">
          <div className="modal-content">
            <div className="modal-header">
              <h5 className="modal-title" id="speakerModalLabel">
                {selectedSpeaker ? 'Edit Speaker' : 'Add Speaker'}
              </h5>
              <button type="button" className="btn-close" onClick={closeModal}></button>
            </div>
            <div className="modal-body">
              <div className="mb-3">
                <label className="form-label">Name</label>
                <input
                  type="text"
                  className="form-control"
                  name="name"
                  value={formData.name || ''}
                  onChange={handleChange}
                />
              </div>
              <div className="mb-3">
                <label className="form-label">Bio</label>
                <textarea
                  className="form-control"
                  name="bio"
                  value={formData.bio || ''}
                  onChange={handleChange}
                />
              </div>
              <div className="mb-3">
                <label className="form-label">Event ID</label>
                <input
                  type="text"
                  className="form-control"
                  name="eventId"
                  value={formData.eventId || ''}
                  onChange={handleChange}
                />
              </div>
            </div>
            <div className="modal-footer">
              <button className="btn btn-secondary" onClick={closeModal}>
                Cancel
              </button>
              <button className="btn btn-primary" onClick={handleSaveSpeaker}>
                Save
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default SpeakersPage;
