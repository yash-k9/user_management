import 'dart:async';
import 'package:flutter/material.dart';
import 'package:user_management/home/data/IUserRepository.dart';
import '../model/User.dart';

class HomeViewModel extends ChangeNotifier {
  final IUserRepository _userRepository;
  List<User> _users = [];
  List<User> get users => _users;
  StreamSubscription? _userStreamSubscription;

  bool _isLoading = false;
  bool get isLoading => _isLoading;

  HomeViewModel(this._userRepository) {
    _userStreamSubscription = _userRepository.getUserStream().listen((users) {
      _users = users;
      _isLoading = false;
      notifyListeners();
    });
  }

  Future<void> fetchUsers() async {
    _isLoading = true;
    notifyListeners();
    try {
      _users = await _userRepository.fetchUsers();
    } catch (e) {
      //TODO: Handle error
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> deleteUser(User user) async {
    try {
      await _userRepository.deleteUser(user);
    } catch (e) {
      //TODO: Handle error
    }
  }

  Future<void> addUser() async {
    try {
      await _userRepository.addUser();
    } catch (e) {
      //TODO: Handle error
    }
  }

  @override
  void dispose() {
    _userStreamSubscription?.cancel();
    super.dispose();
  }
}
