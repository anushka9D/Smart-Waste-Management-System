import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthHeader from '../../components/AuthHeader';
import AuthFooter from '../../components/AuthFooter';
import { createWasteRequest } from '../../services/api.js';

function ReportBinForm() {
  const [formData, setFormData] = useState({
    category: '',
    description: '',
    address: '',
    latitude: '',
    longitude: '',
    binId: ''
  });
  const [photo, setPhoto] = useState(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    // Get location data from previous step
    const savedLocation = localStorage.getItem('selectedLocation');
    if (savedLocation) {
      const location = JSON.parse(savedLocation);
      setFormData(prev => ({
        ...prev,
        address: location.address,
        latitude: location.latitude,
        longitude: location.longitude
      }));
    }
  }, []);

  const categories = [
    { value: 'OVERFLOWING_BIN', displayName: 'Overflowing Bin' },
    { value: 'DAMAGED_BIN', displayName: 'Damaged Bin' },
    { value: 'MISSING_BIN', displayName: 'Missing Bin' },
    { value: 'ILLEGAL_DUMPING', displayName: 'Illegal Dumping' },
    { value: 'REGULAR_PICKUP_REQUEST', displayName: 'Regular Pickup Request' },
    { value: 'OTHER', displayName: 'Other' }
  ];

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    
    // Clear custom category description when not "OTHER"
    if (name === 'category' && value !== 'OTHER') {
      setFormData(prev => ({
        ...prev,
        customCategory: ''
      }));
    }
  };

  const handlePhotoChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      // Validate file type and size
      if (!file.type.startsWith('image/')) {
        alert('Please select an image file');
        return;
      }
      if (file.size > 5 * 1024 * 1024) { // 5MB limit
        alert('Image size should be less than 5MB');
        return;
      }
      setPhoto(file);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!formData.category || !formData.description || !formData.address) {
      alert('Please fill in all required fields');
      return;
    }

    setLoading(true);
    try {
      const submitData = new FormData();
      submitData.append('category', formData.category);
      submitData.append('description', formData.description);
      submitData.append('address', formData.address);
      submitData.append('latitude', formData.latitude);
      submitData.append('longitude', formData.longitude);
      if (formData.binId) {
        submitData.append('binId', formData.binId);
      }
      if (formData.category === 'OTHER' && formData.customCategory) {
        submitData.append('customCategory', formData.customCategory);
      }
      if (photo) {
        submitData.append('photo', photo);
      }

      const response = await createWasteRequest(submitData);

      if (response.success) {
        // Clear stored location
        localStorage.removeItem('selectedLocation');
        // Navigate to confirmation page with data
        navigate('/citizen/report-confirmation', {
          state: {
            requestData: response.data
          }
        });
      }
      else {
        alert(response.message || 'Failed to submit request');
      }
    } catch (error) {
      console.error('Error submitting request:', error);
      alert('Failed to submit request. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex flex-col">
      <AuthHeader />

      <main className="flex-grow bg-gray-50 py-8 px-4">
        <div className="container mx-auto max-w-2xl">
          <div className="bg-white rounded-lg shadow-md p-6">
            <h1 className="text-2xl font-bold text-gray-800 mb-6">Submit Waste Disposal Request</h1>

            <form onSubmit={handleSubmit}>
              {/* Category */}
              <div className="mb-6">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Waste Category (type of issue) *
                </label>
                <select
                  name="category"
                  value={formData.category}
                  onChange={handleInputChange}
                  required
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                >
                  <option value="">Select a category</option>
                  {categories.map(cat => (
                    <option key={cat.value} value={cat.value}>
                      {cat.displayName}
                    </option>
                  ))}
                </select>
              </div>

              {/* Custom Category Description (only when OTHER is selected) */}
              {formData.category === 'OTHER' && (
                <div className="mb-6">
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Describe Category *
                  </label>
                  <input
                    type="text"
                    name="customCategory"
                    value={formData.customCategory || ''}
                    onChange={handleInputChange}
                    required
                    placeholder="Please describe the category"
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                  />
                </div>
              )}

              {/* Bin ID (Optional) */}
              <div className="mb-6">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Bin ID (Optional)
                </label>
                <input
                  type="text"
                  name="binId"
                  value={formData.binId}
                  onChange={handleInputChange}
                  placeholder="Enter bin ID if applicable (Check the sticker pasted on the bin)"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                />
              </div>

              {/* Description */}
              <div className="mb-6">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Description *
                </label>
                <textarea
                  name="description"
                  value={formData.description}
                  onChange={handleInputChange}
                  required
                  rows="4"
                  placeholder="Describe the waste disposal issue or request..."
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                />
              </div>

              {/* Location Display */}
              <div className="mb-6">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Selected Location
                </label>
                <div className="bg-gray-50 rounded-lg p-4">
                  <p className="text-sm text-gray-900">{formData.address}</p>
                  {formData.latitude && formData.longitude && (
                    <p className="text-xs text-gray-500 mt-1">
                      Coordinates: {formData.latitude.toFixed(6)}, {formData.longitude.toFixed(6)}
                    </p>
                  )}
                </div>
                <button
                  type="button"
                  onClick={() => navigate('/citizen/report-bin-request')}
                  className="mt-2 text-sm text-green-600 hover:text-green-800"
                >
                  Change Location
                </button>
              </div>

              {/* Photo Upload */}
              <div className="mb-6">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Photo Evidence (Optional)
                </label>
                <input
                  type="file"
                  accept="image/*"
                  onChange={handlePhotoChange}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                />
                <p className="text-xs text-gray-500 mt-1">
                  Upload a photo of the waste issue (Max 5MB, JPG/PNG)
                </p>
                {photo && (
                  <div className="mt-2">
                    <p className="text-sm text-green-600">Selected: {photo.name}</p>
                  </div>
                )}
              </div>

              {/* Action Buttons */}
              <div className="flex justify-between pt-4">
                <button
                  type="button"
                  onClick={() => navigate('/citizen/report-bin-request')}
                  className="px-6 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition"
                >
                  ← Back
                </button>
                <button
                  type="submit"
                  disabled={loading}
                  className="px-6 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition disabled:bg-gray-400"
                >
                  {loading ? (
                    <>
                      <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2 inline-block"></div>
                      Submitting...
                    </>
                  ) : (
                    'Submit Request'
                  )}
                </button>
              </div>
            </form>
          </div>
        </div>
      </main>

      <AuthFooter />
    </div>
  );
}

export default ReportBinForm;