import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthHeader from '../../components/AuthHeader';
import AuthFooter from '../../components/AuthFooter';
import { getMyFeedback } from '../../services/api';

function FeedbackList() {
  const navigate = useNavigate();
  const [feedbackList, setFeedbackList] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchFeedback();
  }, []);

  const fetchFeedback = async () => {
    try {
      const response = await getMyFeedback();
      if (response.success) {
        setFeedbackList(response.data);
      } else {
        console.error('Failed to fetch feedback:', response.message);
      }
    } catch (error) {
      console.error('Error fetching feedback:', error);
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const getTopicColor = (topic) => {
    const colors = {
      'Service Quality': 'bg-blue-100 text-blue-800',
      'Response Time': 'bg-green-100 text-green-800',
      'Staff Behavior': 'bg-purple-100 text-purple-800',
      'Communication': 'bg-yellow-100 text-yellow-800',
      'Overall Experience': 'bg-indigo-100 text-indigo-800',
      'Other': 'bg-gray-100 text-gray-800'
    };
    return colors[topic] || 'bg-gray-100 text-gray-800';
  };

  if (loading) {
    return (
      <div className="min-h-screen flex flex-col">
        <AuthHeader />
        <main className="flex-grow bg-gray-50 flex items-center justify-center">
          <div className="text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600 mx-auto"></div>
            <p className="mt-4 text-gray-600">Loading your feedback...</p>
          </div>
        </main>
        <AuthFooter />
      </div>
    );
  }

  return (
    <div className="min-h-screen flex flex-col">
      <AuthHeader />
      <main className="flex-grow bg-gray-50 py-8 px-4">
        <div className="container mx-auto max-w-4xl">
          <div className="bg-white rounded-lg shadow-md p-6">
            <div className="flex justify-between items-start mb-6">
              <div>
                <h1 className="text-2xl font-bold text-gray-800">My Feedback</h1>
                <p className="text-gray-600">View and manage your submitted feedback</p>
              </div>
              <button
                onClick={() => navigate('/citizen-dashboard')}
                className="px-4 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition text-sm"
              >
                ← Back to Dashboard
              </button>
            </div>

            {feedbackList.length === 0 ? (
              <div className="text-center py-12">
                <div className="text-5xl mb-4">📝</div>
                <h3 className="text-xl font-semibold text-gray-800 mb-2">No Feedback Yet</h3>
                <p className="text-gray-600 mb-6">You haven't submitted any feedback yet.</p>
                <button
                  onClick={() => navigate('/citizen-dashboard')}
                  className="px-6 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition"
                >
                  Back to Dashboard
                </button>
              </div>
            ) : (
              <div className="space-y-4">
                {feedbackList.map(feedback => (
                  <div key={feedback.id} className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition">
                    <div className="flex justify-between items-start">
                      <div>
                        <div className="flex items-center space-x-2 mb-2">
                          <span className={`px-2 py-1 rounded-full text-xs font-medium ${getTopicColor(feedback.topic)}`}>
                            {feedback.topic}
                          </span>
                          <div className="flex">
                            {[...Array(5)].map((_, i) => (
                              <span key={i} className={`text-lg ${i < feedback.rating ? 'text-yellow-400' : 'text-gray-300'}`}>
                                ★
                              </span>
                            ))}
                          </div>
                        </div>
                        {feedback.comment && (
                          <p className="text-gray-700 mb-3">{feedback.comment}</p>
                        )}
                        <p className="text-xs text-gray-500">
                          Submitted on {formatDate(feedback.submittedAt)}
                        </p>
                      </div>
                      {feedback.photoUrl && (
                        <div className="ml-4 flex-shrink-0">
                          <img 
                            src={feedback.photoUrl} 
                            alt="Feedback" 
                            className="w-16 h-16 object-cover rounded-lg"
                          />
                        </div>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </main>
      <AuthFooter />
    </div>
  );
}

export default FeedbackList;