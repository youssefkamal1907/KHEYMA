import React, { useState, useEffect } from 'react';
import {
    LayoutDashboard,
    Tent,
    CalendarDays,
    Users,
    DollarSign,
    Settings,
    LogOut,
    Search,
    Bell,
    Plus,
    Filter,
    RotateCw,
    Pencil,
    Trash2,
    MoreHorizontal,
    Shield,
    UserCheck,
    User,
    ChevronLeft,
    ChevronRight,
    History,
    Ban
} from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import { adminAPI } from '../services/api';
import { useNavigate } from 'react-router-dom';

const AdminDashboard = () => {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const [activeTab, setActiveTab] = useState('all');

    // State for real data
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [pagination, setPagination] = useState({
        page: 0,
        size: 10,
        totalPages: 0,
        totalElements: 0,
        first: true,
        last: false
    });

    // Redirect if not admin
    useEffect(() => {
        if (user && user.role !== 'ROLE_ADMIN') {
            navigate('/');
        }
    }, [user, navigate]);

    // Fetch users on mount or page change
    useEffect(() => {
        fetchUsers();
    }, [pagination.page]);

    const fetchUsers = async () => {
        try {
            setLoading(true);
            const response = await adminAPI.getUsers({
                page: pagination.page,
                size: pagination.size
            });
            // Handle PageResponse structure
            const data = response.data;
            setUsers(data.content || []);
            setPagination(prev => ({
                ...prev,
                totalPages: data.totalPages,
                totalElements: data.totalElements,
                first: data.first,
                last: data.last
            }));
        } catch (error) {
            console.error("Failed to fetch users:", error);
        } finally {
            setLoading(false);
        }
    };

    const handlePageChange = (newPage) => {
        if (newPage >= 0 && newPage < pagination.totalPages) {
            setPagination(prev => ({ ...prev, page: newPage }));
        }
    };

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    const formatDate = (dateString) => {
        if (!dateString) return 'N/A';
        return new Date(dateString).toLocaleDateString('en-US', {
            month: 'short', day: 'numeric', year: 'numeric'
        });
    };

    const getAvatar = (name) => {
        return `https://ui-avatars.com/api/?name=${encodeURIComponent(name || 'User')}&background=random`;
    };

    const getDisplayRole = (roles) => {
        if (!roles || roles.length === 0) return 'Guest';
        if (roles.includes('ROLE_ADMIN')) return 'Admin'; // Or Super Admin if distinguishable
        if (roles.includes('ROLE_USER')) return 'Registered User';
        return 'Guest';
    };

    const getRoleBadge = (roles) => {
        const role = getDisplayRole(roles);
        switch (role) {
            case 'Super Admin': // Placeholder case if we had Super Admin
            case 'Admin':
                return (
                    <span className="flex items-center gap-1.5 px-3 py-1 rounded-lg text-xs font-medium bg-purple-500/10 text-purple-400 border border-purple-500/20">
                        <Shield size={12} className="fill-purple-400" />
                        Admin
                    </span>
                );
            case 'Registered User':
                return (
                    <span className="flex items-center gap-1.5 px-3 py-1 rounded-lg text-xs font-medium bg-blue-500/10 text-blue-400 border border-blue-500/20">
                        <UserCheck size={12} className="fill-blue-400" />
                        Registered User
                    </span>
                );
            default:
                return (
                    <span className="flex items-center gap-1.5 px-3 py-1 rounded-lg text-xs font-medium bg-gray-500/10 text-gray-400 border border-gray-500/20">
                        <User size={12} />
                        Guest
                    </span>
                );
        }
    };

    const getStatusBadge = (active) => {
        if (active) {
            return (
                <span className="flex items-center gap-1.5 text-xs font-medium text-green-400">
                    <div className="w-1.5 h-1.5 rounded-full bg-green-400" />
                    Active
                </span>
            );
        } else {
            return (
                <span className="flex items-center gap-1.5 text-xs font-medium text-yellow-400">
                    <div className="w-1.5 h-1.5 rounded-full bg-yellow-400" />
                    Inactive
                </span>
            );
        }
    };

    return (
        <div className="flex h-screen bg-[#102213] text-gray-100 font-body overflow-hidden">
            {/* Sidebar */}
            <aside className="w-64 border-r border-[#28392b] flex flex-col bg-[#102213]">
                <div className="p-6 flex items-center gap-3">
                    <div className="w-8 h-8 rounded-full bg-primary flex items-center justify-center">
                        <Tent className="text-[#102213]" size={18} />
                    </div>
                    <span className="font-display font-bold text-lg text-white">Kheyma Admin</span>
                </div>

                <nav className="flex-1 px-3 py-4 space-y-1">
                    <NavItem icon={LayoutDashboard} label="Dashboard" />
                    <NavItem icon={Tent} label="Campsites" />
                    <NavItem icon={CalendarDays} label="Bookings" badge="5" />
                    <NavItem icon={Users} label="Users" active />
                    <NavItem icon={DollarSign} label="Financials" />
                    <NavItem icon={Settings} label="Settings" />
                </nav>

                <div className="p-4 border-t border-[#28392b]">
                    <div className="flex items-center gap-3 p-2 rounded-xl hover:bg-[#1a2c1e] transition-colors cursor-pointer group">
                        <img
                            src={getAvatar(user?.name)}
                            alt="Admin"
                            className="w-10 h-10 rounded-full border-2 border-[#28392b] group-hover:border-primary transition-colors"
                        />
                        <div className="flex-1 min-w-0">
                            <p className="text-sm font-medium text-white truncate">{user?.name || 'Admin'}</p>
                            <p className="text-xs text-gray-400 truncate">{user?.email}</p>
                        </div>
                        <LogOut size={18} className="text-gray-400 group-hover:text-red-400 transition-colors" onClick={handleLogout} />
                    </div>
                </div>
            </aside>

            {/* Main Content */}
            <main className="flex-1 flex flex-col min-w-0 overflow-hidden">
                {/* Header */}
                <header className="h-16 border-b border-[#28392b] flex items-center justify-between px-8 bg-[#102213]">
                    <div className="relative w-96">
                        <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={18} />
                        <input
                            type="text"
                            placeholder="Search user accounts..."
                            className="w-full h-10 pl-10 pr-4 bg-[#1c271d] border border-[#28392b] rounded-lg text-sm text-gray-200 focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition-all placeholder:text-gray-500"
                        />
                    </div>

                    <button className="w-10 h-10 rounded-lg bg-[#1c271d] border border-[#28392b] flex items-center justify-center text-gray-400 hover:text-white hover:border-gray-500 transition-all relative">
                        <Bell size={20} />
                        <span className="absolute top-2.5 right-2.5 w-2 h-2 bg-red-500 rounded-full border border-[#1c271d]"></span>
                    </button>
                </header>

                {/* Content Scrollable Area */}
                <div className="flex-1 overflow-y-auto p-8">

                    {/* Page Header */}
                    <div className="flex items-end justify-between mb-8">
                        <div>
                            <h1 className="text-3xl font-display font-bold text-white mb-2">User Management</h1>
                            <p className="text-gray-400">Manage access, roles, and profiles for all Kheyma platform users.</p>
                        </div>
                        <button className="flex items-center gap-2 px-4 py-2 bg-primary hover:bg-primary-hover text-[#102213] text-sm font-medium rounded-lg transition-colors">
                            <Plus size={18} />
                            Add New User
                        </button>
                    </div>

                    {/* Filters Bar */}
                    <div className="flex flex-wrap items-center gap-4 mb-6">
                        <div className="flex bg-[#1c271d] p-1 rounded-lg border border-[#28392b]">
                            <TabButton active={activeTab === 'all'} onClick={() => setActiveTab('all')}>All Users</TabButton>
                            <TabButton active={activeTab === 'admins'} onClick={() => setActiveTab('admins')}>Admins</TabButton>
                            <TabButton active={activeTab === 'registered'} onClick={() => setActiveTab('registered')}>Registered</TabButton>
                            <TabButton active={activeTab === 'guests'} onClick={() => setActiveTab('guests')}>Guests</TabButton>
                        </div>

                        <div className="ml-auto flex items-center gap-3">
                            <button
                                className="flex items-center gap-2 px-3 py-2 bg-[#1c271d] border border-[#28392b] rounded-lg text-sm text-gray-300 hover:text-white hover:border-gray-500 transition-all"
                                onClick={fetchUsers}
                                title="Refresh"
                            >
                                <RotateCw size={16} className={loading ? "animate-spin" : ""} />
                                Refresh
                            </button>
                            <button className="flex items-center gap-2 px-3 py-2 bg-[#1c271d] border border-[#28392b] rounded-lg text-sm text-gray-300 hover:text-white hover:border-gray-500 transition-all">
                                <Filter size={16} />
                                Filter by Status
                            </button>
                        </div>
                    </div>

                    {/* Table */}
                    <div className="bg-[#1c271d] border border-[#28392b] rounded-xl overflow-hidden min-h-[400px]">
                        {loading && users.length === 0 ? (
                            <div className="flex items-center justify-center h-64 text-gray-400">
                                Loading users...
                            </div>
                        ) : (
                            <table className="w-full text-left text-sm">
                                <thead className="bg-[#1a2e1e] border-b border-[#28392b]">
                                    <tr>
                                        <th className="py-3 px-6 font-medium text-gray-400 w-10">
                                            <input type="checkbox" className="rounded bg-[#28392b] border-[#28392b] text-primary focus:ring-0 focus:ring-offset-0" />
                                        </th>
                                        <th className="py-3 px-6 font-medium text-gray-400">USER</th>
                                        <th className="py-3 px-6 font-medium text-gray-400">ROLE</th>
                                        <th className="py-3 px-6 font-medium text-gray-400">STATUS</th>
                                        <th className="py-3 px-6 font-medium text-gray-400">JOINED DATE</th>
                                        <th className="py-3 px-6 font-medium text-gray-400">LAST LOGIN</th>
                                        <th className="py-3 px-6 font-medium text-gray-400 text-right">ACTIONS</th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-[#28392b]">
                                    {users.map((user) => (
                                        <tr key={user.id} className="group hover:bg-[#202e22] transition-colors">
                                            <td className="py-4 px-6 text-gray-400">
                                                <input type="checkbox" className="rounded bg-[#28392b] border-[#28392b] text-primary focus:ring-0 focus:ring-offset-0" />
                                            </td>
                                            <td className="py-4 px-6">
                                                <div className="flex items-center gap-3">
                                                    <img src={getAvatar(user.name)} alt="" className="w-9 h-9 rounded-full bg-[#28392b]" />
                                                    <div>
                                                        <p className="font-medium text-gray-200">{user.name || 'No Name'}</p>
                                                        <p className="text-xs text-gray-500">{user.email}</p>
                                                    </div>
                                                </div>
                                            </td>
                                            <td className="py-4 px-6">
                                                {getRoleBadge(user.roles)}
                                            </td>
                                            <td className="py-4 px-6">
                                                {getStatusBadge(user.active)}
                                            </td>
                                            <td className="py-4 px-6">
                                                <p className="text-gray-300">{formatDate(user.createdAt)}</p>
                                            </td>
                                            <td className="py-4 px-6">
                                                <p className="text-gray-300">N/A</p>
                                            </td>
                                            <td className="py-4 px-6 text-right">
                                                <div className="flex items-center justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                                                    <button className="p-1.5 text-gray-400 hover:text-white hover:bg-[#28392b] rounded-lg transition-colors" title="Edit">
                                                        <Pencil size={16} />
                                                    </button>
                                                    <button className="p-1.5 text-gray-400 hover:text-yellow-400 hover:bg-[#28392b] rounded-lg transition-colors" title="History">
                                                        <History size={16} />
                                                    </button>
                                                    <button className="p-1.5 text-gray-400 hover:text-red-400 hover:bg-[#28392b] rounded-lg transition-colors" title="Delete">
                                                        <Trash2 size={16} />
                                                    </button>
                                                </div>
                                            </td>
                                        </tr>
                                    ))}
                                    {users.length === 0 && !loading && (
                                        <tr>
                                            <td colSpan="7" className="py-8 text-center text-gray-500">
                                                No users found.
                                            </td>
                                        </tr>
                                    )}
                                </tbody>
                            </table>
                        )}
                    </div>

                    {/* Footer / Pagination */}
                    <div className="flex items-center justify-between mt-4">
                        <p className="text-sm text-gray-400">
                            Showing <span className="font-medium text-gray-200">{users.length}</span> of <span className="font-medium text-gray-200">{pagination.totalElements}</span> users
                        </p>
                        <div className="flex items-center gap-2">
                            <button
                                onClick={() => handlePageChange(pagination.page - 1)}
                                disabled={pagination.first}
                                className="p-2 text-gray-500 hover:text-white disabled:opacity-30 disabled:hover:text-gray-500 transition-colors"
                            >
                                <ChevronLeft size={18} />
                            </button>

                            <span className="text-sm text-gray-400 px-2">
                                Page {pagination.page + 1} of {Math.max(1, pagination.totalPages)}
                            </span>

                            <button
                                onClick={() => handlePageChange(pagination.page + 1)}
                                disabled={pagination.last}
                                className="p-2 text-gray-500 hover:text-white disabled:opacity-30 disabled:hover:text-gray-500 transition-colors"
                            >
                                <ChevronRight size={18} />
                            </button>
                        </div>
                    </div>

                </div>
            </main>
        </div>
    );
};

const NavItem = ({ icon: Icon, label, active, badge }) => (
    <div
        className={`
      group flex items-center justify-between px-3 py-2.5 rounded-lg cursor-pointer transition-all
      ${active
                ? 'bg-[#1c271d] text-primary'
                : 'text-gray-400 hover:bg-[#1a2c1e] hover:text-gray-200'
            }
    `}
    >
        <div className="flex items-center gap-3">
            <Icon size={18} className={active ? 'text-primary' : 'text-gray-500 group-hover:text-gray-300'} />
            <span className="text-sm font-medium">{label}</span>
        </div>
        {badge && (
            <span className="bg-primary text-[#102213] text-[10px] font-bold px-1.5 py-0.5 rounded-md">
                {badge}
            </span>
        )}
    </div>
);

const TabButton = ({ children, active, onClick }) => (
    <button
        onClick={onClick}
        className={`
      px-4 py-1.5 text-sm font-medium rounded-md transition-all
      ${active
                ? 'bg-[#28392b] text-white shadow-sm'
                : 'text-gray-400 hover:text-gray-200'
            }
    `}
    >
        {children}
    </button>
);

export default AdminDashboard;
