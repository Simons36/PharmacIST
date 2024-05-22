import axios from 'axios';

export async function getAddressFromCoordinates(latitude: number, longitude: number, apiKey: string): Promise<string> {
    try {
        const response = await axios.get('https://maps.googleapis.com/maps/api/geocode/json', {
            params: {
                latlng: `${latitude},${longitude}`,
                key: apiKey
            }
        });

        if (response.data.status === 'OK' && response.data.results.length > 0) {
            return response.data.results[0].formatted_address;
        } else {
            throw new Error('Unable to fetch address from coordinates');
        }
    } catch (error) {
        console.error('Error fetching address from Google Geocoding API', error);
        throw new Error('Error fetching address from Google Geocoding API');
    }
}
