import 'dart:async';
import 'package:flutter/material.dart';
import 'package:user_management/home/data/IUserRepository.dart';
import '../model/User.dart';

class HomeViewModel extends ChangeNotifier {
  final IUserRepository _userRepository;
  List<User> _users = [];
  List<User> get users => _users;
  StreamSubscription? _userStreamSubscription;

  bool _isLoading = true;
  bool get isLoading => _isLoading;

  String? _errorMessage;
  String? get errorMessage => _errorMessage;

  HomeViewModel(this._userRepository) {
    _userStreamSubscription = _userRepository.getUserStream().listen((users) {
      _users = users;
      _isLoading = false;
      _errorMessage = null;
      notifyListeners();
    });
  }

  Future<void> fetchUsers() async {
    _isLoading = true;
    _errorMessage = null;
    notifyListeners();
    try {
      _users = await _userRepository.fetchUsers();
    } catch (e) {
      _errorMessage = "Failed to fetch users: ${e.toString()}";
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> deleteUser(User user) async {
    try {
      await _userRepository.deleteUser(user);
    } catch (e) {
      _errorMessage = "Failed to delete user: ${e.toString()}";
      notifyListeners();
    }
  }

  Future<void> addUser() async {
    try {
      await _userRepository.addUser();
    } catch (e) {
      _errorMessage = "Failed to add user: ${e.toString()}";
      notifyListeners();
    }
  }

  void clearError() {
    if (_errorMessage != null) {
      _errorMessage = null;
      notifyListeners();
    }
  }

  @override
  void dispose() {
    _userStreamSubscription?.cancel();
    super.dispose();
  }
}
