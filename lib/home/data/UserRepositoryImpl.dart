import 'dart:async';
import 'package:flutter/services.dart';
import '../model/User.dart';
import 'IUserRepository.dart';

class UserRepositoryImpl implements IUserRepository {
  final MethodChannel _platformMethodChannel;
  final EventChannel _userEventChannel;

  UserRepositoryImpl({
    required MethodChannel platformMethodChannel,
    required EventChannel userEventChannel,
  }) : _platformMethodChannel = platformMethodChannel,
       _userEventChannel = userEventChannel;

  @override
  Stream<List<User>> getUserStream() {
    return _userEventChannel.receiveBroadcastStream().map((data) {
      if (data is List) {
        return data
            .map((e) => User.fromMap(Map<String, dynamic>.from(e)))
            .toList();
      }
      return <User>[];
    });
  }

  @override
  Future<List<User>> fetchUsers() async {
    final data = await _platformMethodChannel.invokeMethod('get_users');
    if (data is List) {
      return data
          .map((e) => User.fromMap(Map<String, dynamic>.from(e)))
          .toList();
    }
    return <User>[];
  }

  @override
  Future<void> addUser() async {
    await _platformMethodChannel.invokeMethod('add_user');
  }

  @override
  Future<void> deleteUser(User user) async {
    await _platformMethodChannel.invokeMethod('delete_user', user.toMap());
  }
}
