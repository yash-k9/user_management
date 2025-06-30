import 'dart:async';
import '../model/User.dart';

abstract class IUserRepository {
  Stream<List<User>> getUserStream();
  Future<List<User>> fetchUsers();
  Future<void> addUser();
  Future<void> deleteUser(User user);
}

