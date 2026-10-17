const doctorPhotos = [
  new URL('../../../../photo/doctor (1).png', import.meta.url).href,
  new URL('../../../../photo/doctor (2).png', import.meta.url).href,
  new URL('../../../../photo/doctor (3).png', import.meta.url).href,
  new URL('../../../../photo/doctor (4).png', import.meta.url).href,
  new URL('../../../../photo/doctor (5).png', import.meta.url).href,
  new URL('../../../../photo/doctor (6).png', import.meta.url).href,
  new URL('../../../../photo/doctor (7).png', import.meta.url).href,
  new URL('../../../../photo/doctor (8).png', import.meta.url).href,
  new URL('../../../../photo/doctor (9).png', import.meta.url).href,
  new URL('../../../../photo/doctor (10).png', import.meta.url).href
]

const normalizeRemoteAvatar = (avatar, baseAPI) => {
  if (!avatar) return ''
  if (avatar.startsWith('http')) return avatar
  return `${baseAPI}${avatar}`
}

const getPhotoIndex = (doctor, fallbackIndex = 0) => {
  const id = Number(doctor?.id)
  if (Number.isInteger(id) && id > 0) {
    return (id - 1) % doctorPhotos.length
  }
  return Math.max(0, fallbackIndex) % doctorPhotos.length
}

export const getDoctorAvatar = (doctor, fallbackIndex = 0, baseAPI = '/api') => {
  const remoteAvatar = normalizeRemoteAvatar(doctor?.user?.avatar || doctor?.avatar, baseAPI)
  return doctorPhotos[getPhotoIndex(doctor, fallbackIndex)] || remoteAvatar
}
