import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import AuthHeader from '../../components/AuthHeader';
import AuthFooter from '../../components/AuthFooter';

function RequestDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [request, setRequest] = useState(null);
  const [loading, setLoading] = useState(true);

  // Status timeline with colors and icons
  const statusTimeline = [
    { status: 'Submitted', color: 'bg-blue-500', icon: '📝' },
    { status: 'Assigned', color: 'bg-yellow-500', icon: '👤' },
    { status: 'Collection Scheduled', color: 'bg-purple-500', icon: '📅' },
    { status: 'Out for collection', color: 'bg-orange-500', icon: '🚚' },
    { status: 'Collected', color: 'bg-green-500', icon: '✅' },
    { status: 'Resolved', color: 'bg-gray-500', icon: '🏁' }
  ];

  useEffect(() => {
    // Generate dummy request data
    const generateDummyRequest = () => {
      const categories = ['Overflowing Bin', 'Damaged Bin', 'Missing Bin', 'Regular Pickup Request'];
      const statuses = ['Submitted', 'Assigned', 'Collection Scheduled', 'Out for collection', 'Collected', 'Resolved'];
      const currentStatus = statuses[Math.floor(Math.random() * statuses.length)];
      const statusIndex = statuses.indexOf(currentStatus);

      return {
        id: id || 'REQ-1001',
        category: categories[Math.floor(Math.random() * categories.length)],
        description: `Request for ${categories[Math.floor(Math.random() * categories.length)].toLowerCase()} at specified location. The bin has been overflowing for several days and needs immediate attention.`,
        status: currentStatus,
        submittedAt: new Date(Date.now() - Math.random() * 7 * 24 * 60 * 60 * 1000).toISOString(),
        location: {
          address: '123 Main Street, Colombo 05',
          latitude: 6.9271,
          longitude: 79.8612
        },
        binId: Math.random() > 0.5 ? `BIN-${Math.floor(Math.random() * 1000)}` : null,
        photo: Math.random() > 0.7 ? 'https://via.placeholder.com/400x300?text=Bin+Photo' : null,
        updates: [
          {
            status: 'Submitted',
            timestamp: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000).toISOString(),
            note: 'Request submitted successfully'
          },
          ...(statusIndex >= 1 ? [{
            status: 'Assigned',
            timestamp: new Date(Date.now() - 4 * 24 * 60 * 60 * 1000).toISOString(),
            note: 'Assigned to collection team A'
          }] : []),
          ...(statusIndex >= 2 ? [{
            status: 'Collection Scheduled',
            timestamp: new Date(Date.now() - 3 * 24 * 60 * 60 * 1000).toISOString(),
            note: 'Scheduled for collection on next business day'
          }] : []),
          ...(statusIndex >= 3 ? [{
            status: 'Out for collection',
            timestamp: new Date(Date.now() - 2 * 24 * 60 * 60 * 1000).toISOString(),
            note: 'Collection team dispatched to location'
          }] : []),
          ...(statusIndex >= 4 ? [{
            status: 'Collected',
            timestamp: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000).toISOString(),
            note: 'Waste successfully collected'
          }] : []),
          ...(statusIndex >= 5 ? [{
            status: 'Resolved',
            timestamp: new Date(Date.now() - 12 * 60 * 60 * 1000).toISOString(),
            note: 'Request completed and resolved'
          }] : [])
        ]
      };
    };

    // Simulate API call
    setTimeout(() => {
      setRequest(generateDummyRequest());
      setLoading(false);
    }, 1000);
  }, [id]);

  const handleCancelRequest = () => {
    if (window.confirm('Are you sure you want to cancel this request? This action cannot be undone.')) {
      alert('Request cancellation feature will be implemented with backend integration.');
      // Navigate back to track requests
      navigate('/citizen/track-requests');
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
              Back to Track Requests
            </button>
          </div>
        </main>
        <AuthFooter />
      </div>
    );
  }

  const currentStatusIndex = statusTimeline.findIndex(item => item.status === request.status);

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
                <p className="text-gray-600">ID: {request.id}</p>
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
                      <p className="text-gray-900">{request.category}</p>
                    </div>
                    <div>
                      <span className="font-medium text-gray-700">Current Status:</span>
                      <p className="text-gray-900 font-medium">{request.status}</p>
                    </div>
                    <div>
                      <span className="font-medium text-gray-700">Submitted:</span>
                      <p className="text-gray-900">
                        {new Date(request.submittedAt).toLocaleString()}
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
                  <p className="text-sm text-gray-900">{request.location.address}</p>
                  <p className="text-xs text-gray-500 mt-1">
                    Lat: {request.location.latitude}, Lng: {request.location.longitude}
                  </p>
                </div>

                {/* Description */}
                <div className="bg-gray-50 rounded-lg p-4">
                  <h2 className="text-lg font-semibold text-gray-800 mb-3">Description</h2>
                  <p className="text-sm text-gray-900">{request.description}</p>
                </div>

                {/* Photo */}
                {request.photo && (
                  <div className="bg-gray-50 rounded-lg p-4">
                    <h2 className="text-lg font-semibold text-gray-800 mb-3">Photo Evidence</h2>
                    <img 
                      src={request.photo} 
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
                      
                      return (
                        <div key={item.status} className="flex items-start space-x-3">
                          {/* Timeline dot */}
                          <div className={`flex-shrink-0 w-8 h-8 rounded-full flex items-center justify-center text-white text-sm ${
                            isCompleted ? item.color : 'bg-gray-300'
                          }`}>
                            {isCompleted ? item.icon : index + 1}
                          </div>
                          
                          {/* Content */}
                          <div className="flex-1">
                            <p className={`font-medium ${
                              isCompleted ? 'text-gray-900' : 'text-gray-400'
                            }`}>
                              {item.status}
                            </p>
                            {isCurrent && (
                              <p className="text-xs text-green-600 font-medium mt-1">Current Status</p>
                            )}
                            {request.updates.find(update => update.status === item.status) && (
                              <p className="text-xs text-gray-500 mt-1">
                                {new Date(
                                  request.updates.find(update => update.status === item.status).timestamp
                                ).toLocaleString()}
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
                  
                  {request.status !== 'Resolved' && request.status !== 'Collected' && (
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