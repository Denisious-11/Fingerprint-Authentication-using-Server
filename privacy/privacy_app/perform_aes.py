from Crypto.Cipher import AES
from Crypto.Util.Padding import pad
import base64
from Crypto.Util.Padding import unpad


def encrypt_image(image_path, encryption_key):
    with open(image_path, "rb") as image_file:
        # Read the image and convert it to bytes
        image_bytes = image_file.read()

    # Create a new AES cipher object with the provided key and AES.MODE_ECB mode
    cipher = AES.new(encryption_key.encode(), AES.MODE_ECB)

    # Pad the image bytes to be a multiple of 16 bytes
    padded_image_bytes = pad(image_bytes, 16)

    # Encrypt the padded image bytes
    encrypted_image_bytes = cipher.encrypt(padded_image_bytes)

    # Convert the encrypted image bytes to a string format
    encrypted_image_string = base64.b64encode(encrypted_image_bytes).decode('utf-8')

    return encrypted_image_string



def decrypt_image(encrypted_image_string, encryption_key):
    # Convert the encrypted image string from base64 to bytes
    encrypted_image_bytes = base64.b64decode(encrypted_image_string)

    # Create a new AES cipher object with the provided key and AES.MODE_ECB mode
    cipher = AES.new(encryption_key.encode(), AES.MODE_ECB)

    # Decrypt the encrypted image bytes
    decrypted_image_bytes = cipher.decrypt(encrypted_image_bytes)

    # Unpad the decrypted image bytes
    unpadded_image_bytes = unpad(decrypted_image_bytes, 16)

    return unpadded_image_bytes
