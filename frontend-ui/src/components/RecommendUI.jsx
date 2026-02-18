import React, { useState } from 'react'
import axios from 'axios'


export default function RecommendUI(){
const [userId, setUserId] = useState('user-123')
const [k, setK] = useState(5)
const [loading, setLoading] = useState(false)
const [result, setResult] = useState(null)
const [error, setError] = useState(null)


const recommendApiBase = process.env.REACT_APP_RECOMMEND_API || 'http://recommend-api-service:3000'


const callRecommend = async () =>{
setLoading(true); setError(null); setResult(null)
try{
const url = `${recommendApiBase}/recommend/${encodeURIComponent(userId)}?k=${encodeURIComponent(k)}`
const res = await axios.get(url, { timeout: 10000 })
setResult(res.data)
}catch(err){
setError(err.message || String(err))
}finally{ setLoading(false) }
}


return (
<div className="bg-white p-6 rounded-2xl shadow">
<div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
<div>
<label className="block text-sm font-medium text-gray-700">User ID</label>
<input value={userId} onChange={e=>setUserId(e.target.value)} className="mt-1 p-2 border rounded w-full" />
</div>
<div>
<label className="block text-sm font-medium text-gray-700">k (top-k)</label>
<input type="number" value={k} onChange={e=>setK(e.target.value)} className="mt-1 p-2 border rounded w-full" />
</div>
<div className="flex items-end">
<button onClick={callRecommend} className="w-full py-2 px-4 bg-indigo-600 text-white rounded hover:bg-indigo-700" disabled={loading}>
{loading ? 'Loading...' : 'Get Recommendations'}
</button>
</div>
</div>


{error && <div className="text-red-600">Error: {error}</div>}


{result && (
<div>
<h3 className="text-lg font-semibold mb-2">Recommendation Response</h3>
<pre className="p-3 bg-gray-100 rounded overflow-auto text-sm">{JSON.stringify(result, null, 2)}</pre>
</div>
)}


{!result && !error && (
<div className="text-sm text-gray-500">No results yet — enter a user id and click <strong>Get Recommendations</strong>.</div>
)}
</div>
)
}