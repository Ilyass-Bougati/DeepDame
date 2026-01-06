import 'package:deepdame/static/Utils.dart';
import 'package:uuid/uuid.dart';

class MessageDTO {
  Uuid uuid = Uuid();
  late String id = uuid.v4();
  late String userId = uuid.v4();
  final String message;
  var createdAt = Utils.getJavaDate();

  MessageDTO(this.message);

  Map<String, String> toJson() {
    return {
      "id": id,
      "userId": userId,
      "message": message,
      "createdAt": createdAt,
    };
  }
}
