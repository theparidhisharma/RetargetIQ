import React from 'react';
import RecommendUI from './components/RecommendUI';

export default function App() {
  return (
    <div className='min-h-screen bg-gray-50 p-6'>
      <div className='max-w-4xl mx-auto'>
        <header className='mb-6'>
          <h1 className='text-3xl font-semibold'>RetargetIQ — Recommendations UI</h1>
          <p className='text-gray-600'>Call the recommend API and view results (works with docker network).</p>
        </header>
        <RecommendUI />
      </div>
    </div>
  );
}
