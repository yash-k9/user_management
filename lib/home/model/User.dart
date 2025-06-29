class User {
  final int id;
  final String name;
  final String phoneNumber;
  final String? profileImagePath;
  final String? signatureBase64;

  User({
    required this.id,
    required this.name,
    required this.phoneNumber,
    this.profileImagePath,
    this.signatureBase64,
  });

  factory User.fromMap(Map<String, dynamic> map) {
    return User(
      id: map['id'] as int,
      name: map['name'] as String,
      phoneNumber: map['phoneNumber'] as String,
      profileImagePath: map['profileImagePath'] as String?,
      signatureBase64: map['signatureBase64'] as String?,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'name': name,
      'phoneNumber': phoneNumber,
      'profileImagePath': profileImagePath,
      'signatureBase64': signatureBase64,
    };
  }
}
