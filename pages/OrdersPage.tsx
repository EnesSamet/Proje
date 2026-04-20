import { Package, Calendar, DollarSign } from 'lucide-react';

const mockOrders = [
  {
    id: 1,
    date: '2026-04-20 12:37:08',
    total: 20.0,
    status: 'Delivered',
    items: [
      { name: 'Soğanlı Cips', quantity: 1, price: 20.0 },
    ],
  },
  {
    id: 2,
    date: '2026-04-20 12:37:18',
    total: 100.0,
    status: 'Processing',
    items: [
      { name: 'Gofret', quantity: 2, price: 10.0 },
      { name: 'Premium Coffee', quantity: 1, price: 45.0 },
      { name: 'Dark Chocolate', quantity: 1, price: 25.0 },
    ],
  },
];

export function OrdersPage() {
  return (
    <div className="min-h-screen bg-gray-50 py-8 px-4">
      <div className="max-w-7xl mx-auto">
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-gray-900 mb-2">Order History</h1>
          <p className="text-gray-600">Track and manage your orders</p>
        </div>

        <div className="space-y-6">
          {mockOrders.map((order) => (
            <div key={order.id} className="bg-white rounded-xl shadow-md overflow-hidden">
              <div className="bg-gradient-to-r from-indigo-50 to-purple-50 px-6 py-4 border-b border-gray-200">
                <div className="flex flex-wrap items-center justify-between gap-4">
                  <div className="flex items-center gap-6">
                    <div>
                      <p className="text-sm text-gray-600">Order ID</p>
                      <p className="font-semibold text-gray-900">#{order.id}</p>
                    </div>
                    <div className="flex items-center gap-2 text-gray-600">
                      <Calendar className="w-4 h-4" />
                      <span className="text-sm">{order.date}</span>
                    </div>
                    <div className="flex items-center gap-2 text-gray-600">
                      <DollarSign className="w-4 h-4" />
                      <span className="text-sm font-semibold">${order.total.toFixed(2)}</span>
                    </div>
                  </div>
                  <div>
                    <span
                      className={`px-4 py-2 rounded-full text-sm font-medium ${
                        order.status === 'Delivered'
                          ? 'bg-green-100 text-green-700'
                          : 'bg-blue-100 text-blue-700'
                      }`}
                    >
                      {order.status}
                    </span>
                  </div>
                </div>
              </div>

              <div className="p-6">
                <h3 className="font-semibold text-gray-900 mb-4 flex items-center gap-2">
                  <Package className="w-5 h-5 text-indigo-600" />
                  Items
                </h3>
                <div className="space-y-3">
                  {order.items.map((item, index) => (
                    <div
                      key={index}
                      className="flex items-center justify-between p-4 bg-gray-50 rounded-lg"
                    >
                      <div>
                        <p className="font-medium text-gray-900">{item.name}</p>
                        <p className="text-sm text-gray-600">Quantity: {item.quantity}</p>
                      </div>
                      <p className="font-semibold text-gray-900">
                        ${(item.price * item.quantity).toFixed(2)}
                      </p>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
