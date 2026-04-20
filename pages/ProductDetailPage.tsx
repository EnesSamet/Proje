import { useParams, useNavigate } from 'react-router';
import { Star, ShoppingCart, ArrowLeft, Package, Truck, Shield } from 'lucide-react';
import { mockProducts } from '../data/products';
import { useCart } from '../context/CartContext';
import { toast } from 'sonner';

export function ProductDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { addToCart } = useCart();

  const product = mockProducts.find((p) => p.id === Number(id));

  if (!product) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-2xl font-bold text-gray-900 mb-4">Product not found</h2>
          <button
            onClick={() => navigate('/products')}
            className="px-6 py-3 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700"
          >
            Back to Products
          </button>
        </div>
      </div>
    );
  }

  const handleAddToCart = () => {
    addToCart(product);
    toast.success(`${product.name} added to cart!`);
  };

  return (
    <div className="min-h-screen bg-gray-50 py-8 px-4">
      <div className="max-w-7xl mx-auto">
        <button
          onClick={() => navigate('/products')}
          className="flex items-center gap-2 text-gray-600 hover:text-indigo-600 mb-8 transition-colors"
        >
          <ArrowLeft className="w-5 h-5" />
          Back to Products
        </button>

        <div className="bg-white rounded-2xl shadow-xl overflow-hidden">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 p-8">
            <div className="relative h-96 lg:h-full bg-gray-100 rounded-xl overflow-hidden">
              <img
                src={product.image}
                alt={product.name}
                className="w-full h-full object-cover"
              />
            </div>

            <div className="flex flex-col">
              <div className="mb-4">
                <span className="inline-block px-3 py-1 bg-indigo-100 text-indigo-600 rounded-full text-sm font-medium mb-4">
                  {product.category}
                </span>
                <h1 className="text-4xl font-bold text-gray-900 mb-4">{product.name}</h1>

                <div className="flex items-center gap-3 mb-6">
                  <div className="flex items-center">
                    {[...Array(5)].map((_, i) => (
                      <Star
                        key={i}
                        className={`w-5 h-5 ${
                          i < Math.floor(product.rating)
                            ? 'text-yellow-400 fill-yellow-400'
                            : 'text-gray-300'
                        }`}
                      />
                    ))}
                  </div>
                  <span className="text-gray-600">
                    {product.rating} ({product.reviews} reviews)
                  </span>
                </div>

                <p className="text-gray-600 text-lg mb-8 leading-relaxed">{product.description}</p>

                <div className="mb-8">
                  <div className="flex items-baseline gap-3 mb-4">
                    <span className="text-4xl font-bold text-gray-900">${product.price}</span>
                  </div>
                  <p className="text-gray-600">
                    <span className="font-semibold">{product.stock}</span> items available
                  </p>
                </div>

                <button
                  onClick={handleAddToCart}
                  className="w-full py-4 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors font-semibold text-lg flex items-center justify-center gap-2 shadow-lg shadow-indigo-500/30"
                >
                  <ShoppingCart className="w-6 h-6" />
                  Add to Cart
                </button>
              </div>

              <div className="mt-8 pt-8 border-t border-gray-200 grid grid-cols-3 gap-4">
                <div className="flex flex-col items-center text-center p-4 bg-gray-50 rounded-lg">
                  <Package className="w-8 h-8 text-indigo-600 mb-2" />
                  <span className="text-xs text-gray-600">Quality Product</span>
                </div>
                <div className="flex flex-col items-center text-center p-4 bg-gray-50 rounded-lg">
                  <Truck className="w-8 h-8 text-indigo-600 mb-2" />
                  <span className="text-xs text-gray-600">Fast Delivery</span>
                </div>
                <div className="flex flex-col items-center text-center p-4 bg-gray-50 rounded-lg">
                  <Shield className="w-8 h-8 text-indigo-600 mb-2" />
                  <span className="text-xs text-gray-600">Secure Payment</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
