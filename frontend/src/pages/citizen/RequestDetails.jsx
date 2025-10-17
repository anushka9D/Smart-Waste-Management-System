import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import AuthHeader from '../../components/AuthHeader';
import AuthFooter from '../../components/AuthFooter';
import { getRequestDetails, getRequestUpdates, cancelRequest } from '../../services/api';

function RequestDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [request, setRequest] = useState(null);
  const [updates, setUpdates] = useState([]);
  const [loading, setLoading] = useState(true);

  const statusDisplay = {
    'SUBMITTED': 'Submitted',
    'ASSIGNED': 'Assigned',
    'COLLECTION_SCHEDULED': 'Collection Scheduled',
    'OUT_FOR_COLLECTION': 'Out for collection',
    'COLLECTED': 'Collected',
    'RESOLVED': 'Resolved',
    'CANCELLED': 'Cancelled'
  };

  const categoryDisplay = {
    'OVERFLOWING_BIN': 'Overflowing Bin',
    'DAMAGED_BIN': 'Damaged Bin',
    'MISSING_BIN': 'Missing Bin',
    'ILLEGAL_DUMPING': 'Illegal Dumping',
    'REGULAR_PICKUP_REQUEST': 'Regular Pickup Request',
    'OTHER': 'Other'
  };

  const statusTimeline = [
    { status: 'SUBMITTED', color: 'bg-blue-500', icon: '📝' },
    { status: 'ASSIGNED', color: 'bg-yellow-500', icon: '👤' },
    { status: 'COLLECTION_SCHEDULED', color: 'bg-purple-500', icon: '📅' },
    { status: 'OUT_FOR_COLLECTION', color: 'bg-orange-500', icon: '🚚' },
    { status: 'COLLECTED', color: 'bg-green-500', icon: '✅' },
    { status: 'RESOLVED', color: 'bg-gray-500', icon: '🏁' }
  ];

  useEffect(() => {
    fetchRequestData();
  }, [id]);

  const fetchRequestData = async () => {
    try {
      const [requestResponse, updatesResponse] = await Promise.all([
        getRequestDetails(id),
        getRequestUpdates(id)
      ]);

      // Handle ApiResponse wrapper structure for request details
      if (requestResponse && requestResponse.success) {
        setRequest(requestResponse.data);
      } else {
        console.error('Failed to fetch request details:', requestResponse ? requestResponse.message : 'Unknown error');
      }
      
      // Handle ApiResponse wrapper structure for updates
      if (updatesResponse && updatesResponse.success) {
        setUpdates(updatesResponse.data);
      } else {
        console.error('Failed to fetch request updates:', updatesResponse ? updatesResponse.message : 'Unknown error');
      }
    } catch (error) {
      console.error('Error fetching request data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCancelRequest = async () => {
    if (window.confirm('Are you sure you want to cancel this request? This action cannot be undone.')) {
      try {
        const response = await cancelRequest(id);
        if (response.success) {
          alert('Request cancelled successfully');
          navigate('/citizen/track-requests');
        } else {
          alert(response.message || 'Failed to cancel request');
        }
      } catch (error) {
        console.error('Error cancelling request:', error);
        alert('Failed to cancel request');
      }
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen flex flex-col">
        <AuthHeader />
        <main className="flex-grow bg-gray-50 flex items-center justify-center">
          <div className="text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600 mx-auto"></div>
            <p className="mt-4 text-gray-600">Loading request details...</p>
          </div>
        </main>
        <AuthFooter />
      </div>
    );
  }

  if (!request) {
    return (
      <div className="min-h-screen flex flex-col">
        <AuthHeader />
        <main className="flex-grow bg-gray-50 flex items-center justify-center">
          <div className="text-center">
            <p className="text-gray-500 text-lg">Request not found.</p>
            <button
              onClick={() => navigate('/citizen/track-requests')}
              className="mt-4 px-6 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition"
            >
              Back to Requests
            </button>
          </div>
        </main>
        <AuthFooter />
      </div>
    );
  }

  const currentStatusIndex = statusTimeline.findIndex(item => item.status === request?.status);

  return (
    <div className="min-h-screen flex flex-col">
      <AuthHeader />
      
      <main className="flex-grow bg-gray-50 py-8 px-4">
        <div className="container mx-auto max-w-4xl">
          <div className="bg-white rounded-lg shadow-md p-6">
            {/* Header */}
            <div className="flex justify-between items-start mb-6">
              <div>
                <h1 className="text-2xl font-bold text-gray-800">Request Details</h1>
                <p className="text-gray-600">ID: {request?.requestId}</p>
              </div>
              <button
                onClick={() => navigate('/citizen/track-requests')}
                className="px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition text-sm"
              >
                ← Back to List
              </button>
            </div>

            <div className="grid md:grid-cols-3 gap-6">
              {/* Left Column - Request Details */}
              <div className="md:col-span-2 space-y-6">
                {/* Basic Information */}
                <div className="bg-gray-50 rounded-lg p-4">
                  <h2 className="text-lg font-semibold text-gray-800 mb-3">Basic Information</h2>
                  <div className="grid md:grid-cols-2 gap-4 text-sm">
                    <div>
                      <span className="font-medium text-gray-700">Category:</span>
                      <p className="text-gray-900">{categoryDisplay[request.category] || request.category}</p>
                    </div>
                    <div>
                      <span className="font-medium text-gray-700">Current Status:</span>
                      <p className="text-gray-900 font-medium">
                        {statusDisplay[request.status] || request.status}
                      </p>
                    </div>
                    <div>
                      <span className="font-medium text-gray-700">Submitted:</span>
                      <p className="text-gray-900">
                        {request.submittedAt ? new Date(request.submittedAt).toLocaleString() : 'N/A'}
                      </p>
                    </div>
                    {request.binId && (
                      <div>
                        <span className="font-medium text-gray-700">Bin ID:</span>
                        <p className="text-gray-900">{request.binId}</p>
                      </div>
                    )}
                  </div>
                </div>

                {/* Location */}
                <div className="bg-gray-50 rounded-lg p-4">
                  <h2 className="text-lg font-semibold text-gray-800 mb-3">Location</h2>
                  <p className="text-sm text-gray-900">{request.address}</p>
                  {request.coordinates && (
                    <p className="text-xs text-gray-500 mt-1">
                      Lat: {request.coordinates.latitude}, Lng: {request.coordinates.longitude}
                    </p>
                  )}
                </div>

                {/* Description */}
                <div className="bg-gray-50 rounded-lg p-4">
                  <h2 className="text-lg font-semibold text-gray-800 mb-3">Description</h2>
                  <p className="text-sm text-gray-900">{request.description}</p>
                </div>

                {/* Photo */}
                {request.photoUrl && (
                  <div className="bg-gray-50 rounded-lg p-4">
                    <h2 className="text-lg font-semibold text-gray-800 mb-3">Photo Evidence</h2>
                    <img 
                      src={request.photoUrl} 
                      alt="Bin condition" 
                      className="rounded-lg max-w-full h-auto max-h-64"
                    />
                  </div>
                )}
              </div>

              {/* Right Column - Status Timeline */}
              <div className="space-y-6">
                {/* Status Timeline */}
                <div className="bg-white border border-gray-200 rounded-lg p-4">
                  <h2 className="text-lg font-semibold text-gray-800 mb-4">Status Timeline</h2>
                  
                  <div className="space-y-4">
                    {statusTimeline.map((item, index) => {
                      const isCompleted = index <= currentStatusIndex;
                      const isCurrent = index === currentStatusIndex;
                      const update = updates.find(u => u.status === item.status);
                      
                      return (
                        <div key={item.status} className="flex items-start space-x-3">
                          <div className={`flex-shrink-0 w-8 h-8 rounded-full flex items-center justify-center text-white text-sm ${
                            isCompleted ? item.color : 'bg-gray-300'
                          }`}>
                            {isCompleted ? item.icon : index + 1}
                          </div>
                          
                          <div className="flex-1">
                            <p className={`font-medium ${
                              isCompleted ? 'text-gray-900' : 'text-gray-400'
                            }`}>
                              {statusDisplay[item.status]}
                            </p>
                            {isCurrent && (
                              <p className="text-xs text-green-600 font-medium mt-1">Current Status</p>
                            )}
                            {update && update.timestamp && (
                              <p className="text-xs text-gray-500 mt-1">
                                {new Date(update.timestamp).toLocaleString()}
                              </p>
                            )}
                          </div>
                        </div>
                      );
                    })}
                  </div>
                </div>

                {/* Actions */}
                <div className="bg-white border border-gray-200 rounded-lg p-4">
                  <h2 className="text-lg font-semibold text-gray-800 mb-3">Actions</h2>
                  
                  {request.status !== 'RESOLVED' && request.status !== 'COLLECTED' && request.status !== 'CANCELLED' && (
                    <button
                      onClick={handleCancelRequest}
                      className="w-full px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition text-sm font-medium"
                    >
                      Cancel Request
                    </button>
                  )}
                  
                  <button
                    onClick={() => navigate('/citizen/track-requests')}
                    className="w-full px-4 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 transition text-sm font-medium mt-2"
                  >
                    Back to All Requests
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
      
      <AuthFooter />
    </div>
  );
}

export default RequestDetails;